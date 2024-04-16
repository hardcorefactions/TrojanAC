package cc.fyre.shard.util.bukkit

import mkremins.fanciful.FancyMessage
import org.bukkit.command.CommandSender

/**
 * @project carnage
 *
 * @date 06/07/2020
 * @author xanderume@gmail.com
 */
abstract class PageBuilder<T>(private var resultsPerPage: Int) {

    init {
        if (this.resultsPerPage <= 0) this.resultsPerPage = 10
    }

    abstract fun getFormat(t: T,index: Int):FancyMessage
    abstract fun getHeader(page: Int,maxPages: Int):FancyMessage?
    abstract fun getFooter(page: Int,maxPages: Int):FancyMessage?
    abstract fun getEmptyResultMessage():String

    fun send(sender: CommandSender,page: Int,results: Collection<T>) {
        this.send(sender,page,results.toMutableList())
    }

    fun send(sender: CommandSender,page: Int,results: List<T>) {

        if (results.isEmpty()) {
            sender.sendMessage(this.getEmptyResultMessage())
            return
        }

        val maxPages = results.size / this.resultsPerPage + 1

        val currentPage = if (page <= 0) 1 else if (page > maxPages) maxPages else page

        this.getHeader(currentPage,maxPages)?.send(sender)

        var i = this.resultsPerPage * (currentPage - 1)

        while (i < this.resultsPerPage * currentPage && i < results.size) {
            this.getFormat(results[i],i).send(sender)
            ++i
        }

        this.getFooter(currentPage,maxPages)?.send(sender)
    }

}
