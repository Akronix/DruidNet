package org.druidanet.druidnet.utils

fun <T> mergeOrderedLists(l1: List<T>, l2: List<T>, compareBy: Comparator<T>): List<T> {
    val mergedList = mutableListOf<T>()
    var i = 0
    var j = 0

    while (i < l1.size && j < l2.size) {
        if ( compareBy.compare(l1[i], l2[j]) <= 0) {
            mergedList.add(l1[i])
            i++
        } else {
            mergedList.add(l2[j])
            j++
        }
    }

    // Add any remaining elements from l1
    while (i < l1.size) {
        mergedList.add(l1[i])
        i++
    }

    // Add any remaining elements from l2
    while (j < l2.size) {
        mergedList.add(l2[j])
        j++
    }

    return mergedList
}