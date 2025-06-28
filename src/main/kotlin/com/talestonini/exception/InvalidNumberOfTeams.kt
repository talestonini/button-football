package com.talestonini.exception

class InvalidNumberOfTeams(numTeams: Int) : RuntimeException("invalid number of teams: $numTeams")