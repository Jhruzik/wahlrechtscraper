package com.jhruzik.wahlrechtscraper

object Main extends App {

  if (args.length == 0 || args(0).toLowerCase() == "bund") {
    println(Sonntagsfrage.bund())
  }
  else if (args(0).toLowerCase() == "state") {
    println(Sonntagsfrage.states())
  }

}
