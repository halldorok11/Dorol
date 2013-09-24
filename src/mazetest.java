/**
 * Created by: Halldór Örn Kristjánsson
 * Date: 9/23/13
 * Time: 11:46 PM
 */
public class mazetest {
    static Maze m;
    static boolean a[];

    public static void main(String[] args){
          for (int i = 0; i < 1000 ; i++){
              test2();
          }
    }

    private static void test1(){
        m = new Maze(16*16);
        a = new boolean[256];
        for (int i = 0; i < 256 ; i++){
            a[i] = false;
        }

        for (Edge e : m.getEdges()){
            a[e.either()] = true;
            a[e.other(e.either())] = true;
        }

        for (int i = 0; i < 256 ; i++){
            if (a[i] == false){
                System.out.println("Non connected vertex :" + i);
            }
        }
    }

    private static void test2(){
        m = new Maze(16*16);
        int x;
        for (Edge e : m.getEdges()){
            x = e.other(e.either())-e.either();
            if (x != 1 && x != 16){
                System.out.println(e.other(e.either())-e.either());
            }
        }
    }
}
