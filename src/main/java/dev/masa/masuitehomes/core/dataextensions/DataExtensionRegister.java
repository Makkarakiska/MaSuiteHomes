package dev.masa.masuitehomes.core.dataextensions;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ExtensionService;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;

public class DataExtensionRegister {

    public static void registerHomeExtension(MaSuiteHomes plugin) {
        try {
            DataExtension yourExtension = new HomeDataExtension(plugin);
            ExtensionService.getInstance().register(yourExtension);
        } catch (NoClassDefFoundError | IllegalStateException | IllegalArgumentException ignored) {
        }
    }
}
