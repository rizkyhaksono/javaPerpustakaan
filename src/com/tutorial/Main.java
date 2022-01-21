package com.tutorial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner userOption = new Scanner(System.in);
        String pilihanUser;
        boolean isLanjutkan = true;

        while(isLanjutkan) {
            clearScreen();
            System.out.println("==== Database Perpustakaan ====\n");
            System.out.println("1.\tLihat seluruh buku");
            System.out.println("2.\tCari data buku");
            System.out.println("3.\tTambah data buku");
            System.out.println("4.\tUbah data buku");
            System.out.println("5.\tHapus data buku");

            System.out.print("\nPilihan anda: ");
            pilihanUser = userOption.next();

            switch (pilihanUser) {
                case "1":
                    System.out.println("\n=========");
                    System.out.println("List Buku");
                    System.out.println("=========");
                    tampilkanData();
                    break;
                case "2":
                    System.out.println("\n=========");
                    System.out.println("Cari Buku");
                    System.out.println("=========");
                    // Cari data
                    break;
                case "3":
                    System.out.println("\n===========");
                    System.out.println("Tambah Buku");
                    System.out.println("===========");
                    // Tambah data
                    break;
                case "4":
                    System.out.println("\n=========");
                    System.out.println("Ubah Buku");
                    System.out.println("=========");
                    // Ubah buku
                    break;
                case "5":
                    System.out.println("\n==========");
                    System.out.println("Hapus Buku");
                    System.out.println("==========");
                    // Hapus buku
                    break;
                default:
                    System.err.println("\nOpsi tidak ada di dalam menu!\nSilahkan pilih antara 1-5!");
            }
            isLanjutkan = getYesorNo("\nApakah Anda Ingin Melanjutkan");
        }
    }

    private static void tampilkanData() throws IOException {
        FileReader fileInput;
        BufferedReader bufferInput;

        try{
            fileInput = new FileReader("database.txt");
            bufferInput = new BufferedReader(fileInput);
        }catch(Exception ex){
            System.out.println("Database Tidak Ditemukan!");
            System.out.println("Silahkan Tambah Data Terlebih Dahulu!");
            return;
        }

        System.out.println("\n| No |\tTahun |\tPenulis                |\tPenerbit               |\tJudul Buku");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        String dataFile = bufferInput.readLine();
        int nomorData = 0;
        while(dataFile != null){
            nomorData++;

            StringTokenizer stringToken = new StringTokenizer(dataFile, ",");

            stringToken.nextToken();
            System.out.printf("| %2d ", nomorData);
            System.out.printf("|\t%4s  ", stringToken.nextToken());
            System.out.printf("|\t%-20s   ", stringToken.nextToken());
            System.out.printf("|\t%-20s   ", stringToken.nextToken());
            System.out.printf("|\t%s   ", stringToken.nextToken());
            System.out.print("\n");

            dataFile = bufferInput.readLine();
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
    }

    private static boolean getYesorNo(String message){
        Scanner userOption = new Scanner (System.in);
        System.out.print("\n" + message + " [y/n]? ");
        String pilihanUser = userOption.next();

        while(!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n"))
        {
            System.err.println("Pilihan anda bukan 'y' dan 'n'");
            System.out.print("\n" + message + " [y/n]? ");
            pilihanUser = userOption.next();
        }
        return pilihanUser.equalsIgnoreCase("y");
    }

    private static void clearScreen() throws IOException {
        try{
            if(System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }else{
                System.out.print("\033\143");
            }
        }catch(Exception ex){
            System.err.println("Tidak bisa clear screen");
        }
    }

}
