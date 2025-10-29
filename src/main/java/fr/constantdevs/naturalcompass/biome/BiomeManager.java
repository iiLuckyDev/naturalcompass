package fr.constantdevs.naturalcompass.biome;

import fr.constantdevs.naturalcompass.NaturalCompass;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeManager {

    private static final List<String> allBiomeNames = new ArrayList<>();

    public static void init() {
        allBiomeNames.clear();
        allBiomeNames.addAll(Registry.BIOME.stream()
                .map(biome -> biome.getKey().asString())
                .collect(Collectors.toList()));
    }

    public static List<String> getBiomeNames() {
        return allBiomeNames;
    }

    public static List<String> getFilteredBiomeNames(World.Environment dimension) {
        List<String> excludedBiomes = NaturalCompass.getInstance().getConfigManager().getExcludedBiomes();
        return allBiomeNames.stream()
                .filter(biomeName -> !excludedBiomes.contains(biomeName))
                .filter(biomeName -> isBiomeInDimension(biomeName, dimension))
                .collect(Collectors.toList());
    }

    public static List<String> getAllBiomeNamesInDimension(World.Environment dimension) {
        return allBiomeNames.stream()
                .filter(biomeName -> isBiomeInDimension(biomeName, dimension))
                .collect(Collectors.toList());
    }

    public static List<String> getProvidersForDimension(World.Environment dimension) {
        return getAllBiomeNamesInDimension(dimension).stream()
                .map(biomeName -> biomeName.split(":")[0])
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getBiomeNamesForProviderAndDimension(String provider, World.Environment dimension) {
        return getAllBiomeNamesInDimension(dimension).stream()
                .filter(biomeName -> biomeName.startsWith(provider + ":"))
                .collect(Collectors.toList());
    }

    private static boolean isBiomeInDimension(String biomeName, World.Environment dimension) {
        String configDim = NaturalCompass.getInstance().getConfigManager().getBiomeDimensions().get(biomeName);
        if (configDim == null) {
            configDim = "overworld"; // default for biomes not in config
        }
        String envString = switch (dimension) {
            case NORMAL -> "overworld";
            case NETHER -> "nether";
            case THE_END -> "end";
            default -> "overworld";
        };
        return configDim.equals(envString);
    }
}