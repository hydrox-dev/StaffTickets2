package hydrox.main;

import hydrox.TicketCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffTickets extends JavaPlugin
{

    public static StaffTickets instance;

    @Override
    public void onEnable()
    {
        instance = this;
        this.getCommand("tickets").setExecutor((CommandExecutor) new TicketCommand());
        this.getServer().getPluginManager().registerEvents(new TicketCommand(), this);
        getLogger().info("StaffTickets v0.0.1 is enabled");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("StaffTickets has been disabled");
        instance = null;
    }

    //Test
}
