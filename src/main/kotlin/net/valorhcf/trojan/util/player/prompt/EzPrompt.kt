package net.valorhcf.trojan.util.player.prompt

import cc.fyre.shard.menu.Menu
import cc.fyre.shard.menu.MenuHandler
import net.valorhcf.trojan.util.player.PlayerUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class EzPrompt<T>(protected val lambda: (T) -> Unit) : StringPrompt() {

    protected var text = "${ChatColor.GREEN}Please input a value."
    private var limit = -1
    private var regex: Regex? = null

    private var menu: Menu? = null
    private var restoreMenu = true
    private var queueMenuUpdate = false

    private var next: Prompt? = null

    private var failureLambda: ((String) -> Unit)? = null
    private var intercept: ((String) -> Boolean)? = null

    private var async: Boolean = false
    private var plugin: JavaPlugin? = null

    fun withText(text: String): EzPrompt<T> {
        this.text = text
        return this
    }

    fun withNextPrompt(prompt: Prompt): EzPrompt<T> {
        this.next = prompt
        return this
    }

    fun withRegex(regex: Regex): EzPrompt<T> {
        this.regex = regex
        return this
    }

    fun withLimit(limit: Int): EzPrompt<T> {
        this.limit = limit
        return this
    }

    fun intercept(intercept: (String) -> Boolean): EzPrompt<T> {
        this.intercept = intercept
        return this
    }

    fun withRestoreMenu(value: Boolean): EzPrompt<T> {
        this.restoreMenu = value
        return this
    }

    fun withMenuUpdate(value: Boolean): EzPrompt<T> {
        this.queueMenuUpdate = value
        return this
    }

    fun onFailure(use: (String) -> Unit): EzPrompt<T> {
        this.failureLambda = use
        return this
    }

    fun async(plugin: JavaPlugin): EzPrompt<T> {
        this.async = true
        this.plugin = plugin
        return this
    }

    override fun getPromptText(context: ConversationContext): String {
        return this.text
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {

        val player = context.forWhom as Player

        if (this.intercept != null && this.intercept!!.invoke(input)) {
            return Prompt.END_OF_CONVERSATION
        }

        if (this.limit != -1 && input.length > this.limit) {
            player.sendRawMessage("${ChatColor.RED}Input text is too long! (${input.length} > ${this.limit})")
            this.failureLambda?.invoke(input)
            return Prompt.END_OF_CONVERSATION
        }

        if (this.regex != null && !input.matches(this.regex!!)) {
            player.sendRawMessage("${ChatColor.RED}Input text does not match regex pattern ${this.regex!!.pattern}.")
            this.failureLambda?.invoke(input)
            return Prompt.END_OF_CONVERSATION
        }

        val value = this.parse(player,input)

        if (value != null) {

            val task = Runnable{

                try {
                    this.lambda.invoke(value)
                } catch (ex: Exception) {
                    player.sendRawMessage("${ChatColor.RED}Failed to handle input: ${ChatColor.WHITE}${input}")
                    this.failureLambda?.invoke(input)
                    ex.printStackTrace()
                }

                if (this.menu != null && this.restoreMenu) {

                    if (this.queueMenuUpdate) {
                        this.menu!!.requiresUpdate = true
                        this.menu!!.update(player)
                    }

                    this.menu!!.open(player)
                }

            }

            if (this.async) {
                Bukkit.getServer().scheduler.runTaskAsynchronously(this.plugin ?: Shard.instance,task)
            } else {
                task.run()
            }

        }

        return this.next ?: Prompt.END_OF_CONVERSATION
    }

    abstract fun parse(player: Player,input: String):T?

    fun start(player: Player) {

        if (this.restoreMenu) {
            this.menu = MenuHandler.getMenuById(player.uniqueId)?.also{player.closeInventory()}
        }

        PlayerUtil.startPrompt(player,this)
    }

    companion object {

        val NAME_PROMPT = "${ChatColor.GREEN}Please input a name. ${ChatColor.GRAY}(Colors supported, limit of 48 characters)"
        val IDENTIFIER_PROMPT = "${ChatColor.GREEN}Please input a unique ID. ${ChatColor.GRAY}(Limit of 16 characters)"
        val IDENTIFIER_REGEX = "[a-zA-Z_\\-0-9]*".toRegex()

    }

}