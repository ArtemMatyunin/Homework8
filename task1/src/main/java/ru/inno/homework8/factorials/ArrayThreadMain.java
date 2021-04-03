package ru.inno.homework8.factorials;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author Артём Матюнин
 * @version 1.0
 * Вычисляем факториалы для каждого элемента пользовательского массива с использованием ThreadPool
 */

public class ArrayThreadMain {

    /**
     * Массив-хранилище результатов вычислений. Чтобы не вычислять факториал для одного и того же числа несколько раз
     * будем сохранять результаты в массиве. Индекс элемента массива = число, для которого рассчитан факториал.
     */
    static private BigInteger[] factorials;

    /**
     * @param b входной элемент пользовательского массива, для которого ведутся расчеты
     *          Проверяет наличие элемента с индексом b в factorials. Если элемент не пустой, то возвращает его значение
     *          Если элемент null, то рассчитывает для числа b факториал и добавляет в factorials под индексом b
     */

    static private BigInteger calculate(int b) {
        if (factorials[b] != null) {
            return factorials[b];
        } else {
            BigInteger result = BigInteger.valueOf(1);
            for (int i = 1; i <= b; i++) {
                result = result.multiply(BigInteger.valueOf(i));
            }
            factorials[b] = result;
        }
        return factorials[b];
    }

    public static void main(String[] args) throws InterruptedException {

        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        /*Выбор оптимального количества потоков пока для меня неочевиден.
        Пробовал cashThreadPool - потоков много, толку мало*/
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        /*Задаем размер массива, для которого будем считать, вводом с консоли*/
        int size;
        System.out.println("Введите количество элементов в массиве");
        size = scanner.nextInt();
        int[] array = new int[size];
        scanner.nextLine();

        //Задаем верхнюю границу размера числа вводом с консоли
        int numbersLevel;
        System.out.println("Введите границу размера числа");
        numbersLevel = scanner.nextInt();

        //Инициализируем factorials максимальным числом и заполняем массив array случайными числами
        factorials = new BigInteger[numbersLevel];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(numbersLevel);
        }
        scanner.nextLine();

        System.out.println("Создан массив " + array.length + " случайных чисел от 0 до "
                + factorials.length + "Нажмите Enter для запуска расчета.");
        scanner.nextLine();

        /* Сохраняю результаты вычислений в ConcurrentHashMap, но что с ней в итоге делать, так и не придумал.
         Она слишком большая, чтобы сохранять  файл или выводить на экран */
        ConcurrentHashMap<Integer, BigInteger> resultCalculate = new ConcurrentHashMap<>(array.length);

        long startTime = System.nanoTime();
        for (int i = 0; i < array.length; i++) {
            int element = array[i];
            int index = i;
            service.submit(() -> resultCalculate.put(index, calculate(element)));
        }
        service.shutdown();

        //использую awaitTermination, чтобы понять, все ли задачи выполнены, чтобы подвести итоги работы программы
        if (service.awaitTermination(100, TimeUnit.SECONDS)) {
            long finishTime = System.nanoTime();
            long result = finishTime - startTime;
            System.out.println("Время работы: " + result);
        }
    }
}
