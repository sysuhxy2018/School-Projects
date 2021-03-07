import java.util.*;
import java.io.*;

public class homework {
    private static int[][] codes = {
            {0, 0, 0},  // dummy
            {1, 0, 0},
            {-1, 0, 0},
            {0, 1, 0},
            {0, -1, 0},
            {0, 0, 1},
            {0, 0, -1},
            {1, 1, 0},
            {1, -1, 0},
            {-1, 1, 0},
            {-1, -1, 0},
            {1, 0, 1},
            {1, 0, -1},
            {-1, 0, 1},
            {-1, 0, -1},
            {0, 1, 1},
            {0, 1, -1},
            {0, -1, 1},
            {0, -1, -1}
    };

    static class Node implements Comparable<Node>{
        int x, y, z;
        int cost;
        int pastCost;
        Node parent;
        Node(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        @Override
        public int compareTo(Node N) {
            return cost - N.cost;
        }
    }

    private static String algo;
    private static int bx, by, bz, sx, sy, sz, gx, gy, gz;
    private static int[][][][] ops;
    private static Node[][][] openState;
    private static Node[][][] closedState;

    // no need to consider multiple cases in a single file. (only one)
    // no need to consider long to replace int. (actually we cannot deal with such a big number because we use multi-dimensional array to speed up)
    // no need to consider bi-direction, every step must obey the action list.
    public static void main(String[] args) throws Exception{
        // long time = System.currentTimeMillis();
        File file = new File("input.txt");
        File data = new File("output.txt");
        Scanner input = new Scanner(file);
        PrintWriter output = new PrintWriter(data);

        algo = input.next();
        bx = input.nextInt();
        by = input.nextInt();
        bz = input.nextInt();

        ops = new int[bx][by][bz][18];
        openState = new Node[bx][by][bz];
        closedState = new Node[bx][by][bz];

        sx = input.nextInt();
        sy = input.nextInt();
        sz = input.nextInt();

        gx = input.nextInt();
        gy = input.nextInt();
        gz = input.nextInt();

        int n = input.nextInt();
        input.nextLine();   // flush \n buffer
        for (int i = 0; i < n; i++) {
            String s = input.nextLine();
            String[] paras = s.split("\\s+");
            int tx = Integer.parseInt(paras[0]);
            int ty = Integer.parseInt(paras[1]);
            int tz = Integer.parseInt(paras[2]);
            for (int j = 3; j < paras.length; j++)
                ops[tx][ty][tz][j - 3] = Integer.parseInt(paras[j]);
        }
        List<int[]> ans = ucs();
        int sum = 0, cnt = ans.size();
        if (cnt == 0)
            output.println("FAIL");
        else {
            for (int[] step : ans)
                sum += step[3];
            output.println(sum);
            output.println(cnt);
            for (int[] step : ans) {
                for (int i = 0; i < step.length; i++) {
                    if (i == 0)
                        output.print(step[i]);
                    else
                        output.print(" " + step[i]);
                }
                output.println();
            }
        }
        output.close();
        // System.out.println("finish time is " + (System.currentTimeMillis() - time) / 1000D);
    }

    private static int calcPast(int op) {
        if (algo.equals("BFS"))
            return 1;
        return op <= 6 ? 10 : 14;
    }

    private static int calcFuture(int px, int py, int pz) {
        if (algo.equals("BFS") || algo.equals("UCS"))
            return 0;
        double dist = Math.sqrt((px - gx) * (px - gx) +
                (py - gy) * (py - gy) +
                (pz - gz) * (pz - gz));
        return (int)(dist * 10);
    }

    private static boolean isLegal(int px, int py, int pz) {
        return px >= 0 && px < bx && py >= 0 && py < by
                && pz >= 0 && pz < bz;
    }

    private static List<int[]> ucs() {
        List<int[]> path = new ArrayList<>();
        PriorityQueue<Node> open;
        Queue<Node> closed;
        open = new PriorityQueue<>();
        closed = new LinkedList<>();
        Node root = new Node(sx, sy, sz);
        root.pastCost = 0;
        root.cost = root.pastCost + calcFuture(sx, sy, sz);
        open.offer(root);
        openState[sx][sy][sz] = root;
        while (!open.isEmpty()) {
            Node cur = open.poll();
            openState[cur.x][cur.y][cur.z] = null;
            if (cur.x == gx && cur.y == gy && cur.z == gz) {
                // System.out.println("success");
                while (cur != null) {
                    int stepCost = cur.parent == null ? 0 : cur.pastCost - cur.parent.pastCost;
                    path.add(0, new int[]{cur.x, cur.y, cur.z, stepCost});
                    cur = cur.parent;
                }
                return path;
            }

            Queue<Node> childs = new LinkedList<>();
            int[] cp = ops[cur.x][cur.y][cur.z];
            for (int i = 0; i < 18; i++) {
                if (cp[i] == 0)
                    break;
                int dx = cur.x + codes[cp[i]][0];
                int dy = cur.y + codes[cp[i]][1];
                int dz = cur.z + codes[cp[i]][2];
                if (isLegal(dx, dy, dz)) {
                    Node child = new Node(dx, dy, dz);
                    child.pastCost = cur.pastCost + calcPast(cp[i]);
                    child.cost = child.pastCost + calcFuture(dx, dy, dz);
                    child.parent = cur;
                    childs.offer(child);
                }
            }
            while (!childs.isEmpty()) {
                Node child = childs.poll();
                if (openState[child.x][child.y][child.z] == null &&
                        closedState[child.x][child.y][child.z] == null) {
                    open.offer(child);
                    openState[child.x][child.y][child.z] = child;
                }
                else if (openState[child.x][child.y][child.z] != null) {
                    Node tmp = openState[child.x][child.y][child.z];
                    if (child.cost < tmp.cost) {
                        open.remove(tmp);
                        openState[tmp.x][tmp.y][tmp.z] = null;
                        open.offer(child);
                        openState[child.x][child.y][child.z] = child;
                    }
                }
                else if (closedState[child.x][child.y][child.z] != null) {
                    Node tmp = closedState[child.x][child.y][child.z];
                    if (child.cost < tmp.cost) {
                        closed.remove(tmp);
                        closedState[tmp.x][tmp.y][tmp.z] = null;
                        open.offer(child);
                        openState[child.x][child.y][child.z] = child;
                    }
                }
            }

            closed.offer(cur);
            closedState[cur.x][cur.y][cur.z] = cur;
        }
        return new ArrayList<>();
    }

}

