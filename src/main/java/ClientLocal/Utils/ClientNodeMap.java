package ClientLocal.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClientNodeMap {

    private HashMap<Integer, Integer> idsToPorts = new HashMap<>();

    private HashMap<Integer, Integer> portsToIds = new HashMap<>();

    private HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();

    public ClientNodeMap (ArrayList<int[]> idPortPairs) {
        for (int i = 0; i < idPortPairs.size(); i++) {
            int[] pair = idPortPairs.get(i);
            this.idsToPorts.put(pair[0], pair[1]);
            this.portsToIds.put(pair[1], pair[0]);
            this.map.put(pair[0], new ArrayList<>());
        }
    }

    public void addClientNodeConnection(int id1, int id2) {
        if (this.map.containsKey(id1)) {
            this.map.get(id1).add(id2);
        }
        else {
            this.map.put(id1, new ArrayList<>(Arrays.asList(id2)));
        }
    }

    public void addClientNodeIdAndPort(int id, int port) {
        this.idsToPorts.put(id, port);
        this.portsToIds.put(port, id);
    }

    public HashMap<Integer, Integer> getIdsToPorts () {
        return this.idsToPorts;
    }

    public HashMap<Integer, Integer> getPortsToIds () {
        return this.portsToIds;
    }

    public HashMap<Integer, ArrayList<Integer>> getMap() {
        return this.map;
    }
}
