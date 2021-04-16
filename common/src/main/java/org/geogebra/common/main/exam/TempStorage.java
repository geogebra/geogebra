package org.geogebra.common.main.exam;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geogebra.common.move.ggtapi.models.Material;

public class TempStorage {

    private Material currentMaterial;
    private int tempMaterialId;
    private Map<Integer, Material> tempMaterials;

    TempStorage() {
    }

    public Material newMaterial() {
        currentMaterial = new Material(nextTempMaterialId(), Material.MaterialType.ggb);
        return currentMaterial;
    }

    private int nextTempMaterialId() {
        return tempMaterialId++;
    }

    /**
     * Saves a copy of the material into the tempMaterials with the correct id.
     */
    public void saveTempMaterial() {
        Material savedMaterial = tempMaterials.get(currentMaterial.getId());
        if (savedMaterial != null && !savedMaterial.getTitle().equals(currentMaterial.getTitle())) {
            currentMaterial.setId(nextTempMaterialId());
        }
        tempMaterials.put(currentMaterial.getId(), new Material(currentMaterial));
    }

    /**
     * @return A copy of the tempMaterials.
     */
    public Collection<Material> collectTempMaterials() {
        return Collections.unmodifiableCollection(tempMaterials.values());
    }

    public void clearTempMaterials() {
        tempMaterialId = 0;
        tempMaterials = new LinkedHashMap<>();
    }

    void setCurrentMaterial(Material material) {
        currentMaterial = material != null ? material : newMaterial();
    }
}
