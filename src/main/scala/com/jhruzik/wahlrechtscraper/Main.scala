package com.jhruzik.wahlrechtscraper

object Main extends App {

  // Match Arguments
  if (args.length > 0) {

    // Sanity Check on User Input
    val args_allowed = Set("bund", "states")
    val error_msg = "Please choose one argument. Either bund or states"
    val arg = args(0).toLowerCase()
    if (args.length > 1) throw new IllegalArgumentException(error_msg)
    if (!args_allowed.contains(arg)) throw new IllegalArgumentException(error_msg)

    // Return Value
    if (arg == "bund") println(Sonntagsfrage.bund())
    if (arg == "states") println(Sonntagsfrage.states())
  } else println(Sonntagsfrage.bund())

}
