package com.winthier.daily;

import java.util.UUID;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler {
    Permission permission = null;

    public Permission getPermission() {
        if (permission == null) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) permission = permissionProvider.getProvider();
        }
        return permission;
    }
}
