package com.winthier.daily;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHandler {
    private Permission permission = null;

    Permission getPermission() {
        if (permission == null) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) permission = permissionProvider.getProvider();
        }
        return permission;
    }
}
