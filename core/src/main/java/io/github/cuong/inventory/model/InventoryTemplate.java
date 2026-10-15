package io.github.cuong.inventory.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryTemplate {
    private final String id;
    private final String name;
    private final String description;
    private final List<PlacedBlock> fixedBlocks;

    public InventoryTemplate(String id, String name, String description, List<PlacedBlock> fixedBlocks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fixedBlocks = fixedBlocks != null ? new ArrayList<>(fixedBlocks) : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<PlacedBlock> getFixedBlocks() {
        return Collections.unmodifiableList(fixedBlocks);
    }
}
