package com.jhruzik.wahlrechtscraper

object Sonntagsfrage {

  // Import Built-Ins
  import scala.util.matching.Regex

  // Import Jsoup-Wrapper
  import net.ruippeixotog.scalascraper.browser.JsoupBrowser
  import net.ruippeixotog.scalascraper.dsl.DSL._
  import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

  // Init Browser
  val browser = JsoupBrowser()

  // Extractor for Bundestag
  def bund(): String = {

    // Get Raw Content
    val content = browser.get("http://www.wahlrecht.de/umfragen/index.htm")

    // Collect Institutes
    val institutes_raw: List[String] = content >> elementList("th[class=\"in\"]") >> text("th")
    val len_institutes = institutes_raw.length


    // Mark Institutes with recent Polls
    val recent_institutes_option = content >> elementList("tr[id=\"datum\"] td") >?> attr("class")
    val recent_institutes_list = recent_institutes_option.map(x => x.getOrElse(None)).tail.take(len_institutes)
    val institute_zip = institutes_raw.zip(recent_institutes_list)

    def mark_recent(t: Tuple2[String, Serializable]): String = {
      val name_new: String = if (t._2 == "dir") t._1 + "*" else t._1;
      name_new
    }

    val institutes = institute_zip.map(x => mark_recent(x))

    // Collect Party Results
    def extract_party(party: String, len_result: Int): List[String] = {
      val query = "tr[id=\"" + party + "\"] td"
      val party_results_full: List[String] = content >> elementList(query) >> text("td")
      val party_results: List[String] = party_results_full.tail.take(len_result)
      party_results
    }

    val poll_map = Map("CDU/CSU" -> extract_party("cdu", len_institutes),
      "SPD" -> extract_party("spd", len_institutes),
      "GRÜNE" -> extract_party("gru", len_institutes),
      "FDP" -> extract_party("fdp", len_institutes),
      "LINKE" -> extract_party("lin", len_institutes),
      "AfD" -> extract_party("afd", len_institutes),
      "SONSTIGE" -> extract_party("son", len_institutes))

    // Get Margins
    val max_index = poll_map.keys.toList.map(x => x.length).max
    val max_columns = institutes.map(x => x.length).max

    // Build Rows
    def build_row(index: String, data: List[String], pad_index: Int, pad_data: Int) = {
      val index_padded = index + (" " * (pad_index - index.length))
      val data_padded = data.map(x => x + (" " * (pad_data - x.length)))
      val row = index_padded + " ‖ " + data_padded.mkString(" | ")
      row
    }

    val rows: List[String] = List(
      build_row("", institutes, max_index, max_columns),
      build_row("CDU/CSU", poll_map("CDU/CSU"), max_index, max_columns),
      build_row("SPD", poll_map("SPD"), max_index, max_columns),
      build_row("FDP", poll_map("FDP"), max_index, max_columns),
      build_row("GRÜNE", poll_map("GRÜNE"), max_index, max_columns),
      build_row("LINKE", poll_map("LINKE"), max_index, max_columns),
      build_row("AfD", poll_map("AfD"), max_index, max_columns),
      build_row("SONSTIGE", poll_map("SONSTIGE"), max_index, max_columns)
    )

    // Build and Return Table
    val table: String = rows.mkString("\n")
    table
  }


  // Extractor for States
  def states() = {

    // Get Raw Content
    val content = browser.get("http://www.wahlrecht.de/umfragen/landtage/index.htm")

    // Collect Parties
    val parties: List[String] = content >> elementList("thead th[class=\"part\"]") >> text("th")
    val len_parties = parties.length

    // Collect States
    val states = content >> elementList("tbody tr th") >> text("th")

    // Collect Data
    def collect_data(state: String) = {

      // Get Raw Data for state
      val state_query = "tr:matches(" + state + "[^-]) td"
      val data_raw = content >> elementList(state_query) >> text("td")
      val data_slice = data_raw.slice(4,data_raw.length)

      // Check if last Data Entry is special and convert if necessary
      val special_regex = "^\\d{1,3} %$".r
      val data_clean = if (special_regex.findFirstIn(data_slice.last) == None) {

        // Define Converter
        def collect_sonst(percent: String) = {
          val replace_pattern = "[^\\d ]".r
          val replace_decimals = ",\\d+".r
          val percent_int = replace_decimals.replaceAllIn(percent, "")
          val percent_split = replace_pattern.replaceAllIn(percent_int, "").trim().split("\\s+")
          val other_sum = percent_split.map(x => x.toInt).sum
          other_sum
        }

        val sonst_sum = collect_sonst(data_slice.last).toString() + " %"
        val data_clean = data_slice.updated(data_slice.length-1, sonst_sum)
        data_clean
      } else data_slice

      // Return Clean Data
      data_clean
    }

    // Get Margins
    val max_index = states.map(x => x.length).max
    val max_column = parties.map(x => x.length).max

    // Build Row
    def build_row(index: String, data: List[String], pad_index: Int, pad_data: Int) = {
      val index_padded = index + (" " * (pad_index - index.length))
      val data_padded = data.map(x => x + (" " * (pad_data - x.length)))
      val row = index_padded + " ‖ " + data_padded.mkString(" | ")
      row
    }

    var rows: List[String] = List(build_row("", parties, max_index, max_column))
    for (state <- states) {
      rows = build_row(state, collect_data(state), max_index, max_column) :: rows
    }

    // Return Rows
    rows.reverse.mkString("\n")

  }

}
