package com.atguigu.tingshu.search.api;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @date: 2024/6/14 14:38
 * @author: yz
 * @version: 1.0
 */
public class CompletableFutureTest {


    public static void main(String[] args) throws ExecutionException, InterruptedException {



        //创建异步任务A--有返回值结果
        CompletableFuture<Integer> futureA = CompletableFuture.supplyAsync(() -> {
            System.out.println("我是a ,我先打印" );

            return 1024;
        });


        /**
         * thenAcceptAsync:基于一个异步对象生成一个新的没有返回值的异步对象
         *  Async：实现基于异步任务futureA的多个子异步任务执行是并行关系
         */
        //基于A创建异步对象B
        CompletableFuture<Void> futureB = futureA.thenAcceptAsync(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("我是B ,我接到A的返回值结果：" + integer);
            }
        });


        //基于A创建异步对象B
        CompletableFuture<Void> futureC = futureA.thenAccept(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {

                System.out.println("我是C ,我接到A的返回值结果：" + integer);
            }
        });




        //编排的操作---可以声明多个异步任务的关系
        //其中包含的三个任务有一个完成了，当前main任务就算结束了
//        CompletableFuture.anyOf(futureA,futureB,futureC);
        //其中包含的三个任务都完成了，当前mian线程的任务才算完成
        CompletableFuture.allOf(futureA,futureB,futureC).join();





//        //创建异步任务--有返回值
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//
//
//            //执行接口调用
//            System.out.println("异步对象有结果执行了。。。。。"+Thread.currentThread().getName());
//            return 1024;
//
//        });
//
//        Integer integer = integerCompletableFuture.get();
//        System.out.println(integer);
//
//        integerCompletableFuture.join();
//
//        System.out.println("main方法"+Thread.currentThread().getName());


        //创建一个异步任务---没有返回值的
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//
//
//            System.out.println("我是没有返回值的异步对象。。。。。");
//
//        });
//
//
//        voidCompletableFuture.join();



    }





}
