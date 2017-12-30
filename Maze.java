/*1.迷路全体を壁にする。
  2.迷路の外に接するマスを除いて、ランダムに選んだ１マスを道にする。
  3.そこから道をランダムに（進もうとしている方向のマスの上下左右１マスが道でなければ）延ばす。
  4.３を可能な限り繰り返す。
  5.迷路の外側を除いて、常に道になっているマスからランダムに１マス選ぶ。
  6.３−５を可能な限り繰り返す。*/

import java.util.Random;
import java.util.Stack;
import java.util.Scanner;
import java.io.*;

public class Maze {

   static int mazeSize = 0;
   //壁: true, 道: false
   static boolean[][] wall;
   //道にしようとしているマスの行。
   static int row;
   //道にしようとしているマスの列
   static int col;
   //既に道にしたマスの行
   static Stack<Integer> rowStack = new Stack<Integer>();
   //既に道にしたマスの列
   static Stack<Integer> colStack = new Stack<Integer>();
   static int usrRow = mazeSize - 1, usrCol = 1, goalRow = 0, goalCol = mazeSize - 2;


   public static void main(String[] args) {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      if(args.length != 1) {
        System.out.println("Please pass the arguments on the commandline.");
        return;
      }

      mazeSize = Integer.parseInt(args[0]);
      wall = new boolean[mazeSize][mazeSize];
      //遊び方を表示
      printUsage();
      //迷路を作成
      createMaze();
      //ユーザーを初期位置に動かす
      resetUsr();
      //ゴールを初期位置に動かす
      resetGoal();

      Scanner scan = new Scanner(System.in);
      String keys = "";
      char key;
      long start = System.currentTimeMillis(), end;

      while(true) {
         printMaze();
         keys = scan.next();
         key = keys.charAt(keys.length() - 1);
         moveUsr(key);

         if(usrRow == goalRow && usrCol == goalCol) {
           end = System.currentTimeMillis();
           printRezult((end - start) / 1000);
           break;
         }
      }
   }

   //新しく迷路を作るメソッド
   static void createMaze() {

      for(int i = 0; i < mazeSize; i++) {
         for(int j = 0; j < mazeSize; j++) {
            wall[i][j] = true;
         }
      }
      //掘る先をランダムに選択
      Random rnd = new Random();
      row = rnd.nextInt(mazeSize - 2) + 1;
      col = rnd.nextInt(mazeSize - 2) + 1;
      wall[row][col] = false;
      rowStack.push(row);
      colStack.push(col);

      boolean continueFlag = true;

      while(continueFlag) {

           extendPath();

           continueFlag = false;

           while(!rowStack.empty() && !colStack.empty()) {
                row = rowStack.pop();
                col = colStack.pop();

                if(canExtendPath()) {
                  continueFlag = true;
                  break;
                }
           }
      }
   }
      //迷路を表示するメソッド
      static void printMaze() {

         for(int i = 0; i < mazeSize; i++) {
            for(int j = 0; j < mazeSize; j++) {
               if(i == usrRow && j == usrCol) {
                 System.out.print("人");
               }else if(i == goalRow && j == goalCol) {
                 System.out.print("GO");
               }else if(wall[i][j]) {
                 System.out.print(" ■");
               }else {
                 System.out.print("  ");
               }
            }
            System.out.println();
         }
      }
      //道を拡張するメソッド
      static void extendPath() {

         boolean extendFlag = true;

         while(extendFlag) {
              extendFlag = extendPathSub();
         }
      }
      //道の拡張に成功したらtrue,失敗したらfalseを返すメソッド
      static boolean extendPathSub() {

         Random rmd = new Random();

         int direction = rmd.nextInt(4);

         for(int i = 0; i < 4; i++) {
            direction = (direction + i) % 4;
            if(canExtendPathWithDir(direction)) {
              movePoint(direction);
              return true;
            }
         }
         return false;
      }
      //指定した方向へ拡張可能ならばtrue,不可能ならばfalseを返すメソッド
      static boolean canExtendPathWithDir(int direction) {

         int exRow = row, exCol = col;

         switch(direction) {
               case 0:
                      exRow--;
                      break;
               case 1:
                      exRow++;
                      break;
               case 2:
                      exCol--;
                      break;
               case 3:
                      exCol++;
                      break;
         }
         if(countSurroundingPath(exRow, exCol) > 1) {
           return false;
         }
         return true;
      }

      static int countSurroundingPath(int row, int col) {

         int num = 0;

         if(row - 1 < 0 || !wall[row - 1][col]) {
           num++;
         }
         if(row + 1 > mazeSize - 1 || !wall[row + 1][col]) {
           num++;
         }
         if(col - 1 < 0 || !wall[row][col - 1]) {
           num++;
         }
         if(col + 1 > mazeSize - 1 || !wall[row][col + 1]) {
           num++;
         }
         return num;
      }

      //指定した方向へ１マスrowとcolを移動させるメソッド
      static void movePoint(int direction) {

         switch(direction) {
               case 0:
                    row--;
                    break;
               case 1:
                    row++;
                    break;
               case 2:
                    col--;
                    break;
               case 3:
                    col++;
                    break;
         }
         wall[row][col] = false;
         rowStack.push(row);
         colStack.push(col);
      }

      //上下左右いずれかの方向へ移動できるならtrue,できないならfalseを返すメソッド
      static boolean canExtendPath() {

         return(canExtendPathWithDir(0) || canExtendPathWithDir(1) || canExtendPathWithDir(2) || canExtendPathWithDir(3));

      }

      //ユーザーを初期値に動かすメソッド
      static void resetUsr() {

         usrRow = mazeSize - 1;
         usrCol = 1;

         while(true) {
              if(wall[usrRow - 1][usrCol]) {
                usrCol++;
              }else{
                break;
              }
         }
         wall[usrRow][usrCol] = false;
      }

      //ゴールを初期値に動かすメソッド
      static void resetGoal() {

         goalRow = 0;
         goalCol = mazeSize - 1;

         while(true) {
              if(wall[goalRow + 1][goalCol]) {
                goalCol--;
              }else{
                break;
              }
         }
         wall[goalRow][goalCol] = false;
      }

      //ユーザーを動かすメソッド
      static void moveUsr(char key) {

         String errMes = "You can not move in here.";
         int exUsrRow = usrRow, exUsrCol = usrCol;

         switch(key) {
               case 'w':
                    exUsrRow--;
                    break;
               case 's':
                    exUsrRow++;
                    break;
               case 'd':
                    exUsrCol++;
                    break;
               case 'a':
                    exUsrCol--;
                    break;
               case 'r':
                    System.out.println("I came back to the start.");
                    resetUsr();
                    return;
               case 'n':
                    System.out.println("Reworked a new Maze.");
                    createMaze();
                    resetUsr();
                    resetGoal();
                    return;
               default:
                    System.out.println(errMes);
                    return;
         }
         if(exUsrRow > mazeSize - 1 || wall[exUsrRow][exUsrCol]) {
           System.out.println(errMes);
           return;
         }
         usrRow = exUsrRow;
         usrCol = exUsrCol;
      }

      //結果を表示するメソッド
      static void printRezult(long secondTime) {

         System.out.println();
         System.out.println("+-+-+-+-+-+-+-+-+-+");
         System.out.println("|c|o|n|g|r|a|t|s|!|");
         System.out.println("+-+-+-+-+-+-+-+-+-+");
         System.out.println();

         System.out.println("Your clear time is " + secondTime + "seconds.");
         System.out.println();
      }

      //遊び方を表示するメソッド
      static void printUsage() {

         System.out.println("Wellcome to " + mazeSize + "*" + mazeSize + "maze.");
         System.out.println();

         System.out.println("Playing ");
         System.out.println("「人」is a player.");
         System.out.println("「GO」is a goal.");
         System.out.println();

         System.out.println("Please push w key and Enter key : Moving forward.");
         System.out.println("Please push s key and Enter key : Moving back.");
         System.out.println("Please push a key and Enter key : Moving left.");
         System.out.println("Please push d key and Enter key : Moving light.");

         System.out.println("Please push r key and Enter key : restart game.");
         System.out.println("Please push n key and Enter key : new game.");
         System.out.println();

         System.out.println("Let's start MAZE!!");
      }
}
