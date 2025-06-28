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

    // seeks the left-most depth only because we assume the tree is balanced
    fun depth(): Int {
        fun depthAcc(node: Tree<A>, acc: Int): Int =
            when (node) {
                is Branch -> depthAcc(node.left, acc+1)
                is Leaf -> acc
            }
        return depthAcc(this, 1)
    }

    fun value(): A =
        when (this) {
            is Branch -> this.value
            is Leaf -> this.value
        }

}