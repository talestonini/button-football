package com.talestonini.datastructure

sealed class Tree<out A> {

    data class Branch<A>(val value: A, val left: Tree<A>, val right: Tree<A>) : Tree<A>()

    data class Leaf<A>(val value: A) : Tree<A>()

    fun <B> map(f: (A) -> B): Tree<B> =
        when (this) {
            is Branch -> Branch(f(value), left.map(f), right.map(f))
            is Leaf -> Leaf(f(value))
        }

    fun toList(): List<A> =
        when (this) {
            is Branch -> {
                val res = mutableListOf(value)
                res += left.toList()
                res += right.toList()
                res
            }
            is Leaf -> listOf(value)
        }

    fun findFirst(p: (A) -> Boolean): A? =
        when (this) {
            is Branch ->
                if (p(value)) value
                else left.findFirst(p) ?: right.findFirst(p)
            is Leaf ->
                if (p(value)) value
                else null
        }

}