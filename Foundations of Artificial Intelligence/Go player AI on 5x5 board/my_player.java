import java.util.*;
import java.io.*;

public class my_player {

    private static int N;
    private static double komi;
    private static int winType;
    private static int maxRemain;
    private static double infinite = 999999999.0;
    private static double _infinite = -999999999.0;

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        N = 5;
        komi = 2.5;
        int[][] pre = new int[N][N];
        int[][] cur = new int[N][N];
        File read = new File("input.txt");
        File write = new File("output.txt");
        Scanner input = new Scanner(read);
        PrintWriter output = new PrintWriter(write);
        int type = input.nextInt();
        winType = type;
        input.nextLine();
        for (int i = 0; i < N; i++) {
            String sa = input.nextLine();
            for (int j = 0; j < N; j++) {
                pre[i][j] = sa.charAt(j) - '0';
            }
        }
        for (int i = 0; i < N; i++) {
            String sa = input.nextLine();
            for (int j = 0; j < N; j++) {
                cur[i][j] = sa.charAt(j) - '0';
            }
        }

        if (empty(pre)) {
            if (type == 1)
                maxRemain = 24;
            else
                maxRemain = 23;
        }
        else {
            File stepRead = new File("step.txt");
            Scanner i2 = new Scanner(stepRead);
            maxRemain = i2.nextInt();
        }
        File stepWrite = new File("step.txt");
        PrintWriter o2 = new PrintWriter(stepWrite);

        int[] op;
        if (type == 1) {
            if (maxRemain <= 12)
                op = alpha_beta(pre, cur, type, Math.min(maxRemain, 6));
            else
                op = ideep(pre, cur, type);
        }
        else {
            if (maxRemain <= 11)
                op = alpha_beta(pre, cur, type, Math.min(maxRemain, 5));
            else
                op = ideep(pre, cur, type);
        }
        if (op.length == 0)
            output.println("PASS");
        else
            output.println(op[0] + "," + op[1]);
        output.close();

        o2.println(maxRemain - 2);
        o2.close();
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);
    }

    private static boolean empty(int[][] a) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] != 0)
                    return false;
            }
        }
        return true;
    }

    private static int captures(int[][] cur, int type) {
        return find_died_pieces(cur, type).size();
    }

    private static int gcap;
    private static List<int[]> galist;

    private static boolean corner(int i, int j) {
        return (i == 0 && j == 0) || (i == 0 && j == 4) || (i == 4 && j == 0) || (i == 4 && j == 4);
    }

    private static boolean edge(int i, int j) {
        if (corner(i, j))
            return false;
        return i == 0 || i == 4 || j == 0 || j == 4;
    }

    private static void ldfs(int[][] pre, int[][] cur, int type, int l, List<int[]> alist, int cap) {
        if (l == 0) {
            if (cap > gcap) {
                gcap = cap;
                galist = new ArrayList<>(alist);
            }
            else if (cap == gcap) {
                int[] start = alist.get(0);
                if (!corner(start[0], start[1])) {
                    int[] start2 = galist.get(0);
                    if (!corner(start2[0], start2[1]) && !edge(start2[0], start2[1])) {
                        // do nothing
                    }
                    else {
                        galist = new ArrayList<>(alist);
                    }
                }
            }
            return;
        }
        List<int[]> actions = getActions(pre, cur, type);
        for (int[] action : actions) {
            int[][] ppre = cur;
            int[][] ccur = copy_board(cur);
            alist.add(action);
            ccur[action[0]][action[1]] = type;
            if (danger(cur, action[0], action[1])) {
                alist.remove(alist.size() - 1);
                continue;
            }
            int ncap = captures(ccur, 3 - type);
            remove_died_pieces(ccur, 3 - type);
            ldfs(ppre, ccur, type, l - 1, alist, cap + ncap);
            alist.remove(alist.size() - 1);
        }
    }

    private static boolean danger(int[][] cur, int i, int j) {
        return get_liberty(cur, i, j).size() == 1;
    }
    
    private static int[] ideep(int[][] pre, int[][] cur, int type) {
        if (empty(pre) && type == 1)
            return new int[]{2, 2};
        gcap = -1;
        galist = new ArrayList<>();
        int m = 4;
        for (int i = 1; i <= m; i++) {
            int[][] ppre = pre;
            int[][] ccur = copy_board(cur);
            ldfs(ppre, ccur, type, i, new ArrayList<>(), 0);
            if (gcap > 0) {
                /*int[] start2 = galist.get(0);
                if (!corner(start2[0], start2[1]) && !edge(start2[0], start2[1]))*/
                    return galist.get(0);
            }
        }
        // PASS
        if (galist.size() == 0)
            return new int[0];
        else
            return galist.get(0);
    }

    private static int[] alpha_beta(int[][] pre, int[][] cur, int type, int level){
        double v = _infinite;
        double a = _infinite, b = infinite;
        int ind = -1;
        List<int[]> actions = getActions(pre, cur, type);
        // tian-yuan for black
        if (actions.size() == 25)
            return new int[]{2, 2};
        int[][] ppre, ccur;
        for (int i = 0; i < actions.size(); i++) {
            int[] action = actions.get(i);
            ppre = cur;
            ccur = copy_board(cur);
            ccur[action[0]][action[1]] = type;
            List<int[]> eatenOppo = remove_died_pieces(ccur, 3 - type);
            if (type == 1)
                v = Math.max(v, min_value(ppre, ccur, 3 - type, a, b, level - 1, 0, eatenOppo.size()));
            else
                v = Math.max(v, min_value(ppre, ccur, 3 - type, a, b, level - 1, eatenOppo.size(), 0));
            // v cannot >= infinite
            if (v > a) {
                a = v;
                ind = i;
            }
        }
        return ind < 0 ? new int[]{} : actions.get(ind);
    }

    private static double max_value(int[][] pre, int[][] cur, int type, double a, double b, int level, int eaten_1, int eaten_2) {
        double v = _infinite;
        List<int[]> actions = getActions(pre, cur, type);
        if (level == 0 || actions.size() == 0)
            return winner_heuristic(cur, winType, eaten_1, eaten_2);
        int[][] ppre, ccur;
        for (int[] action : actions) {
            ppre = cur;
            ccur = copy_board(cur);
            ccur[action[0]][action[1]] = type;
            List<int[]> eatenOppo = remove_died_pieces(ccur, 3 - type);
            if (type == 1)
                v = Math.max(v, min_value(ppre, ccur, 3 - type, a, b, level - 1, eaten_1, eaten_2 + eatenOppo.size()));
            else
                v = Math.max(v, min_value(ppre, ccur, 3 - type, a, b, level - 1, eaten_1 + eatenOppo.size(), eaten_2));
            if (v >= b)
                return v;
            a = Math.max(a, v);
        }
        return v;
    }

    private static double min_value(int[][] pre, int[][] cur, int type, double a, double b, int level, int eaten_1, int eaten_2) {
        double v = infinite;
        List<int[]> actions = getActions(pre, cur, type);
        if (level == 0 || actions.size() == 0)
            return winner_heuristic(cur, winType, eaten_1, eaten_2);
        int[][] ppre, ccur;
        for (int[] action : actions) {
            ppre = cur;
            ccur = copy_board(cur);
            ccur[action[0]][action[1]] = type;
            List<int[]> eatenOppo = remove_died_pieces(ccur, 3 - type);
            if (type == 1)
                v = Math.min(v, max_value(ppre, ccur, 3 - type, a, b, level - 1, eaten_1, eaten_2 + eatenOppo.size()));
            else
                v = Math.min(v, max_value(ppre, ccur, 3 - type, a, b, level - 1, eaten_1 + eatenOppo.size(), eaten_2));
            if (v <= a)
                return a;
            b = Math.min(b, v);
        }
        return v;
    }

    private static List<int[]> getActions(int[][] pre, int[][] cur, int type) {
        List<int[]> valid_pos = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (valid_place_check(pre, cur, i, j, type))
                    valid_pos.add(new int[]{i, j});
            }
        }
        return valid_pos;
    }

    private static boolean compare_board(int[][] a, int[][] b) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }

    private static int[][] copy_board(int[][] a) {
        int[][] b = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                b[i][j] = a[i][j];
        }
        return b;
    }

    private static List<int[]> detect_neighbor(int[][] cur, int i, int j) {
        List<int[]> neighbors = new ArrayList<>();
        if (i > 0)
            neighbors.add(new int[]{i - 1, j});
        if (i < N - 1)
            neighbors.add(new int[]{i + 1, j});
        if (j > 0)
            neighbors.add(new int[]{i, j - 1});
        if (j < N - 1)
            neighbors.add(new int[]{i, j + 1});
        return neighbors;
    }

    private static List<int[]> detect_neighbor_ally(int[][] cur, int i, int j) {
        List<int[]> neighbors = detect_neighbor(cur, i, j);
        List<int[]> group_allies = new ArrayList<>();
        for (int[] piece : neighbors) {
            if (cur[piece[0]][piece[1]] == cur[i][j])
                group_allies.add(piece);
        }
        return group_allies;
    }

    private static List<int[]> ally_dfs(int[][] cur, int i, int j) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{i, j});
        List<int[]> ally_members = new ArrayList<>();
        boolean[][] visit = new boolean[N][N];
        while (!stack.isEmpty()) {
            int[] piece = stack.pop();
            ally_members.add(piece);
            visit[piece[0]][piece[1]] = true;
            List<int[]> neighbor_allies = detect_neighbor_ally(cur, piece[0], piece[1]);
            for (int[] ally : neighbor_allies) {
                if (!visit[ally[0]][ally[1]])
                    stack.add(ally);
            }
        }
        return ally_members;
    }

    private static boolean find_liberty(int[][] cur, int i, int j) {
        List<int[]> ally_members = ally_dfs(cur, i, j);
        for (int[] member : ally_members) {
            List<int[]> neighbors = detect_neighbor(cur, member[0], member[1]);
            for (int[] piece : neighbors) {
                if (cur[piece[0]][piece[1]] == 0)
                    return true;
            }
        }
        return false;
    }

    private static List<int[]> find_died_pieces(int[][] cur, int type) {
        List<int[]> died_pieces = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cur[i][j] == type) {
                    if (!find_liberty(cur, i, j))
                        died_pieces.add(new int[]{i, j});
                }
            }
        }
        return died_pieces;
    }

    private static List<int[]> remove_died_pieces(int[][] cur, int type) {
        List<int[]> died_pieces = find_died_pieces(cur, type);
        remove_certain_pieces(cur, died_pieces);
        return died_pieces;
    }

    private static void remove_certain_pieces(int[][] cur, List<int[]> positions) {
        for (int[] piece : positions) {
            // modify cur directly
            cur[piece[0]][piece[1]] = 0;
        }
    }

    private static boolean valid_place_check(int[][] pre, int[][] cur, int i, int j, int type) {
        if (i < 0 || i >= N)
            return false;
        if (j < 0 || j >= N)
            return false;
        if (cur[i][j] != 0)
            return false;

        // modify test_board, cur does not change
        int[][] test_board = copy_board(cur);
        test_board[i][j] = type;
        if (find_liberty(test_board, i, j))
            return true;

        remove_died_pieces(test_board, 3 - type);
        if (!find_liberty(test_board, i, j))
            return false;
        else {
            if (compare_board(pre, test_board))
                return false;
        }
        return true;
    }

    private static int score(int[][] cur, int type) {
        int cnt = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cur[i][j] == type)
                    cnt++;
            }
        }
        return cnt;
    }

    private static List<int[]> get_liberty(int[][] cur, int i, int j) {
        List<int[]> allies = ally_dfs(cur, i, j);
        boolean[][] visit = new boolean[N][N];
        List<int[]> liberties = new ArrayList<>();
        for (int[] ally : allies) {
            List<int[]> neighbors = detect_neighbor_ally(cur, ally[0], ally[1]);
            for (int[] piece : neighbors) {
                if (cur[piece[0]][piece[1]] == 0 && !visit[piece[0]][piece[1]]) {
                    liberties.add(piece);
                    visit[piece[0]][piece[1]] = true;
                }
            }
        }
        return liberties;
    }

    private static double winner_heuristic(int[][] cur, int type, int eaten_1, int eaten_2) {
        int cnt_1 = score(cur, 1);
        int cnt_2 = score(cur, 2);
        if (type == 1)
            return cnt_1 - (cnt_2 + komi);
        else
            return (cnt_2 + komi) - cnt_1;
    }
}