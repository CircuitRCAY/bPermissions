package de.bananaco.bpermissions.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CalculableMeta extends GroupCarrier {

    Map<String, String> effectiveMeta;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected CalculableMeta(Set<String> groups, Set<Permission> permissions,
            String world) {
        super(groups, permissions, world);
        effectiveMeta = Collections.synchronizedMap(new HashMap<String, String>());
    }

    /**
     * Used to calculate the total permissions gained by the object
     *
     * @throws RecursiveGroupException
     */
    public synchronized void calculateEffectiveMeta() throws RecursiveGroupException {
        try {
            // Implement meta priorities
            effectiveMeta.clear();

            Map<String, Integer> pr = new HashMap<String, Integer>();

            for (String gr : serialiseGroups()) {
                Group group = WorldManager.getInstance().getWorld(getWorld()).getGroup(gr);
                // Calculate down the tree of the child group
                group.calculateEffectiveMeta();
                Map<String, String> meta = group.getEffectiveMeta();
                Set<String> keySet = meta.keySet();

                for (String key : keySet) {
                    // If the effectiveMeta does not contain the key or the priority is greater than the current
                    synchronized (effectiveMeta) {
                        if (!pr.containsKey(key) || group.getPriority() > pr.get(key)) {
                            // Store the priority too!
                            effectiveMeta.put(key, meta.get(key));
                            pr.put(key, group.getPriority());
                        }
                    }
                }
            }

            pr.clear();

            // Obviously local priority wins every time
            Map<String, String> meta = this.getMeta();
            for (String key : meta.keySet()) {
                effectiveMeta.put(key, meta.get(key));
            }
        } catch (StackOverflowError e) {
            throw new RecursiveGroupException(this);
        }
    }

    public Map<String, String> getEffectiveMeta() {
        return effectiveMeta;
    }

    /**
     * Here you go MiracleM4n! Returns the stored "effective" meta value
     * (calculated with inheritance/priority)
     *
     * @param key
     * @return String (or "" if no value)
     */
    public String getEffectiveValue(String key) {
        if (containsEffectiveValue(key)) {
            return effectiveMeta.get(key);
        }
        return "";
    }

    public boolean containsEffectiveValue(String key) {
        return effectiveMeta.containsKey(key);
    }

    @Override
    public void clear() {
        this.effectiveMeta.clear();
        super.clear();
    }
}
