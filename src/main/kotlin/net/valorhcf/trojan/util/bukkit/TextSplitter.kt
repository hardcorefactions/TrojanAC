package cc.fyre.shard.util.bukkit

import org.bukkit.ChatColor
import java.util.regex.Pattern

object TextSplitter {

    private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§'.toString() + "[0-9A-FK-OR]")

    @JvmStatic
    fun stripColor(input: String): String {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("")
    }

    @JvmStatic
    fun split(length: Int = 32, firstLineLength: Int = length, text: String, linePrefix: String = ChatColor.GRAY.toString(), wordSuffix: String = " "): List<String> {
        if (text.length <= length) {
            return arrayListOf(linePrefix + text)
        }

        val lines = ArrayList<String>()
        val split = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var builder = StringBuilder(linePrefix)

        splitLoop@ for (i in split.indices) {
            val lineLength = if (i == 0) { firstLineLength } else { length }
            val sequenceAtIndex = split[i]
            val sequenceAtIndexLen = stripColor(
                sequenceAtIndex
            ).length
            if (sequenceAtIndexLen > lineLength) {
                var remainingSequence: String = sequenceAtIndex
                while (stripColor(remainingSequence).length >= lineLength) {
                    val substring = remainingSequence.substring(0, lineLength)
                    remainingSequence = remainingSequence.substring(lineLength - 1, remainingSequence.length)
                    lines.add(substring)
                }

                if (remainingSequence.isNotEmpty()) {
                    builder.append(remainingSequence)
                    builder.append(wordSuffix)
                }

                continue@splitLoop
            }

            if (builder.length + sequenceAtIndexLen >= lineLength) {
                lines.add(builder.toString())
                builder = StringBuilder(linePrefix)
            }

            builder.append(sequenceAtIndex)
            builder.append(wordSuffix)

            if (i == split.size - 1) {
                builder.replace(builder.length - wordSuffix.length, builder.length, "")
            }
        }

        if (builder.isNotEmpty()) {
            lines.add(builder.toString())
        }

        return lines
    }

}