package cc.fyre.shard.util

import java.util.*
import kotlin.math.roundToInt


object NumberUtil {

    private val numberToRoman = TreeMap<Int,String>().also{
        it[1] = "I"
        it[4] = "IV"
        it[5] = "V"
        it[9] = "IX"
        it[10] = "X"
        it[40] = "XL"
        it[50] = "L"
        it[90] = "XC"
        it[100] = "C"
        it[400] = "CD"
        it[500] = "D"
        it[900] = "CM"
        it[1000] = "M"
    }

    private val romanToNumber = this.numberToRoman.entries
        .associate{it.value to it.key}
        .toMap(TreeMap())

    @JvmStatic
    fun isInteger(input: String): Boolean {

        return try {
            input.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }

    }

    @JvmStatic
    fun isShort(input: String): Boolean {

        return try {
            input.toShort()
            true
        } catch (e: NumberFormatException) {
            false
        }

    }

    @JvmStatic
    fun toRoman(int: Int): String {

        val number = this.numberToRoman.floorKey(int)

        if (int == number) {
            return this.numberToRoman[int] ?: ""
        }

        return "${this.numberToRoman[number] ?: ""}${this.toRoman(int - number)}"
    }

    @JvmStatic
    fun fromRoman(text: String):Int {

        var sum = 0
        val length = text.length

        var i = 0
        while (i < length) {
            // If present value is less than next value,
            // subtract present from next value and add the
            // resultant to the sum variable.
            if (i != length - 1 && this.romanToNumber[text[i].toString()]!! < this.romanToNumber[text[i + 1].toString()]!!) {
                sum += this.romanToNumber[text[i + 1].toString()]!! - this.romanToNumber[text[i].toString()]!!
                i++
            } else {
                sum += this.romanToNumber[text[i].toString()]!!
            }
            i++
        }

        return sum
    }

    @JvmStatic
    fun roundToHalf(value: Double): Double {
        return (value * 2).roundToInt() / 2.0
    }

}