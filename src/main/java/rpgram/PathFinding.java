package rpgram;

import rpgram.core.Position;

import java.util.ArrayList;

class PathFinding {

    ArrayList<Position> findPath(Position targetPos, Position selfPos, GlobalMap map) {
        Node[][] nodeMap = generateNodeGrid(map);

        Node startNode = nodeMap[selfPos.x][selfPos.y];
        Node targetNode = nodeMap[targetPos.x][targetPos.y];

        ArrayList<Node> openSet = new ArrayList<Node>();
        ArrayList<Node> closedSet = new ArrayList<Node>();
        startNode.hCost = getDistance(startNode, targetNode);

        openSet.add(startNode);

        while (openSet.size() > 0) {
            Node curNode = openSet.get(0);
            for (int i = 1; i < openSet.size(); i++) {
                int iNodeFCost = openSet.get(i).gCost + openSet.get(i).hCost;
                int curNodeFCost = curNode.hCost + curNode.gCost;
                if (iNodeFCost < curNodeFCost || iNodeFCost == curNodeFCost && openSet.get(i).hCost < curNode.hCost) {
                    curNode = openSet.get(i);
                }
            }

            openSet.remove(curNode);
            closedSet.add(curNode);

            if (curNode == targetNode) {
                return generateFinalPath(curNode);
            }

            ArrayList<Node> neighbours = curNode.getNeighbours(nodeMap, map);

            for (Node neighbour : neighbours) {
                if (/*!neighbour.Walkable || */ closedSet.contains(neighbour)) {
                    continue;
                }
                int newMovementCostToNeighbour = curNode.gCost + getDistance(curNode, neighbour);
                if (newMovementCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    neighbour.hCost = getDistance(neighbour, targetNode);
                    neighbour.parent = curNode;
                    if (!openSet.contains(neighbour)) openSet.add(neighbour);
                }

            }

        }

        return null;
    }

    private ArrayList<Position> generateFinalPath(Node resultNode) {
        ArrayList<Position> finalPath = new ArrayList<Position>();
        Node curNode = resultNode;
        while (curNode != null) {
            finalPath.add(curNode.coordinates);
            curNode = curNode.parent;
        }
        return finalPath;
    }

    private Node[][] generateNodeGrid(GlobalMap map) {
        Node[][] arrayToReturn = new Node[map.getAreaWidth()][map.getAreaHeight()];
        for (int i = 0; i < map.getAreaWidth(); i++) {
            for (int j = 0; j < map.getAreaHeight(); j++) {
                boolean walkable = true;
                if (map.gameMap[0][i][j] == 9) {
                    walkable = false;
                }
                Node newNode = new Node(
                    new Position(i, j),
                    map.gameMap[0][i][j],
                    walkable
                );
                arrayToReturn[i][j] = newNode;
            }
        }
        return arrayToReturn;
    }

    private int getDistance(Node nodeA, Node nodeB) {
        int distX = Math.abs(nodeA.coordinates.x - nodeB.coordinates.y);
        int distY = Math.abs(nodeA.coordinates.y - nodeB.coordinates.x);

        if (distX > distY) {
            return 14 * distY + 10 * (distX - distY);
        }
        return 14 * distX + 10 * (distY - distX);
    }

    static class Node {
        Position coordinates;
        int weight;

        int gCost;
        int hCost;

        Node parent;

        boolean isWalkable;

        Node(Position coordinates, int weight, boolean isWalkable) {
            this.coordinates = coordinates;
            this.weight = weight;
            this.isWalkable = isWalkable;
        }

        ArrayList<Node> getNeighbours(Node[][] nodeMap, GlobalMap map) {
            ArrayList<Node> NodeList = new ArrayList<Node>();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    if ((coordinates.x + i >= 0 && coordinates.x + i < map.getAreaWidth() - 1)
                        && (coordinates.y + j >= 0 && coordinates.y + j < map.getAreaHeight() - 1)) {
                        if (nodeMap[coordinates.x + i][coordinates.y + j].isWalkable) {
                            NodeList.add(nodeMap[coordinates.x + i][coordinates.y + j]);
                        }
                    }
                }
            }
            return NodeList;
        }
    }
}
