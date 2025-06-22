package com.talestonini.datastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TreeTest {

    private val leafTree = Tree.Leaf(1)
    private val twoLevelTree = Tree.Branch(1, Tree.Leaf(2), Tree.Leaf(3))
    private val threeLevelTree = Tree.Branch(
        1,
        Tree.Branch(2, Tree.Leaf(3), Tree.Leaf(4)),
        Tree.Branch(5, Tree.Leaf(6), Tree.Leaf(7))
    )

    @Test
    fun mapLeafTree() {
        assertThat(leafTree.map { it * 2 }).isEqualTo(Tree.Leaf(2))
    }

    @Test
    fun mapTwoLevelTree() {
        assertThat(twoLevelTree.map { it * 3 }).isEqualTo(Tree.Branch(3, Tree.Leaf(6), Tree.Leaf(9)))
    }

    @Test
    fun mapThreeLevelTree() {
        assertThat(threeLevelTree.map { it.toString() }).isEqualTo(
            Tree.Branch(
                "1",
                Tree.Branch("2", Tree.Leaf("3"), Tree.Leaf("4")),
                Tree.Branch("5", Tree.Leaf("6"), Tree.Leaf("7"))
            )
        )
    }

    @Test
    fun toListLeafTree() {
        assertThat(leafTree.toList()).isEqualTo(listOf(1))
    }

    @Test
    fun toListTwoLevelTree() {
        assertThat(twoLevelTree.toList()).isEqualTo(listOf(1, 2, 3))
    }

    @Test
    fun toListThreeLevelTree() {
        assertThat(threeLevelTree.toList()).isEqualTo(listOf(1, 2, 3, 4, 5, 6, 7))
    }

    @Test
    fun findFirstInLeafTree() {
        assertThat(leafTree.findFirst { it == 1 }).isEqualTo(1)
    }

    @Test
    fun findFirstInTwoLevelTree() {
        assertThat(twoLevelTree.findFirst { it == 2 }).isEqualTo(2)
    }

    @Test
    fun findFirstInThreeLevelTree() {
        assertThat(threeLevelTree.findFirst { it == 5 }).isEqualTo(5)
    }

    @Test
    fun depthLeafTree() {
        assertThat(leafTree.depth()).isEqualTo(1)
    }

    @Test
    fun depthTwoLevelTree() {
        assertThat(twoLevelTree.depth()).isEqualTo(2)
    }

    @Test
    fun depthThreeLevelTree() {
        assertThat(threeLevelTree.depth()).isEqualTo(3)
    }

}