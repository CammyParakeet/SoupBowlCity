package city.soupbowl.dev.commands

import cc.aviara.annotations.AviaraBean
import cc.aviara.api.command.AviaraCommand
import cc.aviara.api.command.CommandContext
import cc.aviara.core.commands.AbstractAviaraCommand
import com.google.auto.service.AutoService

@AviaraCommand(name = "testthing")
class TestCommand : AbstractAviaraCommand("testthing") {
    override fun execute(ctx: CommandContext): Boolean {
        ctx.sender.sendMessage("Test command in kotlin worked!")

        return false
    }
}