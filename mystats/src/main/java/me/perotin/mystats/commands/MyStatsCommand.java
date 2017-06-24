package me.perotin.mystats.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.perotin.mystats.MyStats;

public class MyStatsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(args.length == 0){
				if(MyStats.instance.inDatabase(player)){
					MyStats.instance.showGui(player, player.getName());
					return true;
					
				}else{
					player.kickPlayer("Kicked! Rejoin to enter database.");
				}
			}else if(args.length == 1){
				if(MyStats.instance.inDatabase(args[0])){
					
					MyStats.instance.showGui(player, args[0]);
					return true;
					
				}else{
					player.sendMessage(ChatColor.RED + "Player not in database!");
					return true;
				}
			}else{
				player.sendMessage(ChatColor.RED + "Improper usage! /mystats <player>");
				return true;
			}
		}else{
			sender.sendMessage("Players only!");
			return true;
		}
		
		
		return true;
	}

}
