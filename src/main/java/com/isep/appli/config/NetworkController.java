package com.isep.appli.config;

import com.isep.appli.dbModels.Friendship;
import com.isep.appli.dbModels.User;
import com.isep.appli.services.FriendshipService;
import com.isep.appli.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.isep.appli.dbModels.FriendshipStatus.*;

@RestController
public class NetworkController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/network/nodes")
    public List<Map<String, Object>> getNodes() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", user.getId());
            node.put("label", user.getUsername());
            nodes.add(node);
        }
        return nodes;
    }


    @GetMapping("/network/edges")
    public List<Map<String, Object>> getEdges() {
        List<Friendship> friendships = friendshipService.getAllFriendships();
        List<Map<String, Object>> edges = new ArrayList<>();
        for (Friendship friendship : friendships) {
            if (friendship.getStatus() == ACCEPTED) {
                Long fromId = friendship.getUser().getId();
                Long toId = friendship.getFriend().getId();
                Map<String, Object> edge = new HashMap<>();
                edge.put("from", fromId);
                edge.put("to", toId);
                Map<String, Object> reverseEdge = new HashMap<>();
                reverseEdge.put("from", toId);
                reverseEdge.put("to", fromId);
                if (!edges.contains(edge) && !edges.contains(reverseEdge)) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }


}


