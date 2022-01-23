package com.tutorial;

// Memakai * agar include semua library
import java.io.*;
import java.time.Year;
import java.util.*;

// Library untuk deleteData
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                    cariData();
                    break;
                case "3":
                    System.out.println("\n===========");
                    System.out.println("Tambah Buku");
                    System.out.println("===========");
                    tambahData();
                    tampilkanData();
                    break;
                case "4":
                    System.out.println("\n=========");
                    System.out.println("Ubah Buku");
                    System.out.println("=========");
                    updateData();
                    break;
                case "5":
                    System.out.println("\n==========");
                    System.out.println("Hapus Buku");
                    System.out.println("==========");
                    deleteData();
                    break;
                default:
                    System.err.println("Opsi tidak ada di dalam menu!\nSilahkan pilih antara 1-5!\n");
            }
            isLanjutkan = getYesorNo("Apakah anda ingin melanjutkan? ");
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
            tambahData();
            return;
        }

        System.out.println("\n| No |\tTahun |\tPenulis                |\tJudul Buku               |\tPenerbit");
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

    private static void cariData() throws IOException {

        // Membaca database ada atau tidak
        try {
            File file = new File("database.txt");
        }catch(Exception e){
            System.err.println("Database Tidak Ditemukan!");
            System.err.println("Silahkan Tambah Data Terlebih Dahulu");
            tambahData();
            return;
        }

        // Kita ambil keyword dari user
        Scanner userOption = new Scanner(System.in);
        System.out.print("Masukkan kata kunci untuk mencari buku: ");
        String cariString = userOption.nextLine();
        System.out.println("Anda mencari: " + cariString);

        String[] keywords = cariString.split("\\s+");

        // Kita cek keyword di database
        cekBukuDiDatabase(keywords, true);

    }

    private static void tambahData() throws IOException {

        // Membuka database di file txt
        FileWriter fileOutput = new FileWriter("database.txt", true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

        // Membaca inputan dari user
        Scanner userOption = new Scanner(System.in);
        String penulis, judul, penerbit, tahun;

        // Memasukkan inputan dari user
        System.out.print("Masukkan nama penulis: ");
        penulis = userOption.nextLine();
        System.out.print("Masukkan judul buku: ");
        judul = userOption.nextLine();
        System.out.print("Masukkan nama penerbit: ");
        penerbit = userOption.nextLine();
        System.out.print("Masukkan tahun terbit: ");
        tahun = ambilTahun();

        // Cek buku di database
        String[] keywords = {tahun+","+penulis+","+penerbit+","+judul};
        System.out.println(Arrays.toString(keywords));

        boolean isExist = cekBukuDiDatabase(keywords, false);

        // Menulis data di database
        if(!isExist){
            // rizkyhaksono_2012_1,2012,rizky haksono,media kita,jejak langkah

            System.out.println(ambilEntryPerTahun(penulis, tahun));
            long nomorEntry = ambilEntryPerTahun(penulis, tahun) + 1;

            String penulisTanpaSpasi = penulis.replaceAll("\\s+", "");
            String primaryKey = penulisTanpaSpasi+"_"+tahun+"_"+nomorEntry;

            System.out.println("\nData yang akan anda masukan adalah");
            System.out.println("----------------------------------------");
            System.out.println("primary key  : " + primaryKey);
            System.out.println("tahun terbit : " + tahun);
            System.out.println("penulis      : " + penulis);
            System.out.println("judul        : " + judul);
            System.out.println("penerbit     : " + penerbit);

            boolean isTambah = getYesorNo("Apakah akan ingin menambah data tersebut? ");

            if(isTambah){
                bufferOutput.write(primaryKey + "," + tahun + "," + penulis + "," + judul + "," + penerbit);
                bufferOutput.newLine();
                bufferOutput.flush();
            }

        }else{
            System.out.println("Buku sudah ada! Dengan data sebagai berikut:");
            cekBukuDiDatabase(keywords, true);
        }
        bufferOutput.close();
    }

    private static void updateData() throws IOException {

        // Ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // Ambil database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // Tampilkan data
        System.out.println("List buku di database");
        tampilkanData();

        // Ambil data input dari pilihan user
        Scanner userOption = new Scanner(System.in);
        System.out.print("\nMasukkan nomor buku yang ingin diupdate: ");
        int updateNum = userOption.nextInt();

        // Tampilkan data yang ingin diupdate
        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while(data != null){
            entryCounts++;

            StringTokenizer st = new StringTokenizer(data, ",");

            // Tampilkan entryCounts == updateNum
            if(updateNum == entryCounts){
                System.out.println("\nData yang ingin anda update adalah:");
                System.out.println("---------------------------------------");
                System.out.println("Referensi           : " + st.nextToken());
                System.out.println("Tahun               : " + st.nextToken());
                System.out.println("Penulis             : " + st.nextToken());
                System.out.println("Penerbit            : " + st.nextToken());
                System.out.println("Judul               : " + st.nextToken());

                String[] fieldData = {"tahun","penulis","penerbit","judul"};
                String[] tempData = new String[4];

                st = new StringTokenizer(data,",");
                String originalData = st.nextToken();

                for(int i=0; i < fieldData.length ; i++) {
                    boolean isUpdate = getYesorNo("Apakah anda ingin merubah " + fieldData[i]);
                    originalData = st.nextToken();
                    if (isUpdate){
                        //user input

                        if (fieldData[i].equalsIgnoreCase("tahun")){
                            System.out.print("Masukkan tahun terbit, format=(YYYY): ");
                            tempData[i] = ambilTahun();
                        } else {
                            userOption = new Scanner(System.in);
                            System.out.print("\nMasukkan " + fieldData[i] + " baru: ");
                            tempData[i] = userOption.nextLine();
                        }

                    } else {
                        tempData[i] = originalData;
                    }
                }

                // tampilkan data baru ke layar
                st = new StringTokenizer(data,",");
                st.nextToken();
                System.out.println("\nData baru anda adalah: ");
                System.out.println("---------------------------------------");
                System.out.println("Tahun               : " + st.nextToken() + " --> " + tempData[0]);
                System.out.println("Penulis             : " + st.nextToken() + " --> " + tempData[1]);
                System.out.println("Penerbit            : " + st.nextToken() + " --> " + tempData[2]);
                System.out.println("Judul               : " + st.nextToken() + " --> " + tempData[3]);

                boolean isUpdate = getYesorNo("Apakah anda yakin ingin mengupdate data? ");

                if (isUpdate){

                    // Cek data baru di database
                    boolean isExist = cekBukuDiDatabase(tempData,false);

                    if(isExist){
                        System.err.println("Data sudah ada di database!\nSilahkan delete data yang bersangkutan");
                        // Copy data
                        bufferedOutput.write(data);

                    } else {
                        // Format data baru kedalam database
                        String tahun = tempData[0];
                        String penulis = tempData[1];
                        String penerbit = tempData[2];
                        String judul = tempData[3];

                        // Primary Key
                        long nomorEntry = ambilEntryPerTahun(penulis, tahun) + 1;

                        String punulisTanpaSpasi = penulis.replaceAll("\\s+","");
                        String primaryKey = punulisTanpaSpasi+"_"+tahun+"_"+nomorEntry;

                        // Tulis data di database
                        bufferedOutput.write(primaryKey + "," + tahun + ","+ penulis +"," + penerbit + ","+judul);
                    }
                } else {
                    // Copy data
                    bufferedOutput.write(data);
                }
            } else {
                // Copy data
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();
            data = bufferedInput.readLine();
        }
        // Meenulis data ke database
        bufferedOutput.flush();
        bufferedOutput.close();
        fileOutput.close();
        bufferedInput.close();
        fileInput.close();

        System.gc();

        // Delete database original
        database.delete();

        // Rename tempDB menjadi database
        tempDB.renameTo(database);
    }

    private static void deleteData() throws IOException {

        // Ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // Buat database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // Tampilkan data
        System.out.println("List data buku");
        tampilkanData();

        // Ambil user input dari data yang ingin dihapus
        Scanner userOption = new Scanner(System.in);
        System.out.print("\nMasukkan data yang ingin dihapus: ");

        long deleteNum = userOption.nextInt();

        // Cek apakah user memasukkan string atau char


        // Looping untuk membaca tiap data baris dan skip untuk data yang ingin dihapus
        boolean isFound = false;
        long entryCounts = 0;

        String data = bufferedInput.readLine();

        while(data != null){
            entryCounts++;

            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data, ",");

            // Tampilkan data yang ingin dihapus
            if(deleteNum == entryCounts){
                System.out.println("\nData yang ingin dihapus:");
                System.out.println("---------------------------");
                System.out.println("Referensi : " + st.nextToken());
                System.out.println("Tahun     : " + st.nextToken());
                System.out.println("Penulis   : " + st.nextToken());
                System.out.println("Penerbit  : " + st.nextToken());
                System.out.println("Judul     : " + st.nextToken());

                isDelete = getYesorNo("Apakah anda yakin untuk menghapus");
                isFound = true;
            }

            if(isDelete){
                // Skip data ke tempDB.txt
                System.out.print("\nData berhasil dihapus!\n");
            }else{
                // Pindahkan data original ke sementara
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if(!isFound){
            System.err.println("Data tidak ditemukan!");
        }

        // Menulis ulang data ke database
        bufferedOutput.flush();
        fileInput.close();
        bufferedInput.close();
        fileOutput.close();
        bufferedOutput.close();
        System.gc();

        // Delete data original
        database.delete();

        // Rename data sementara menjadi data original
        tempDB.renameTo(database);
    }

    private static long ambilEntryPerTahun(String penulis, String tahun) throws IOException{
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        long entry = 0;
        String data = bufferInput.readLine();
        Scanner dataScanner;
        String primaryKey;

        while(data != null){
            dataScanner = new Scanner(data);
            dataScanner.useDelimiter(",");
            primaryKey = dataScanner.next();
            dataScanner = new Scanner(primaryKey);
            dataScanner.useDelimiter("_");

            penulis = penulis.replaceAll("\\s+", "");

            if(penulis.equalsIgnoreCase(dataScanner.next()) && tahun.equalsIgnoreCase(dataScanner.next()) ){
                entry = dataScanner.nextInt();
            }

            data = bufferInput.readLine();
        }
        return entry;
    }

    private static String ambilTahun() throws IOException{
        boolean tahunValid = false;
        Scanner userOption = new Scanner(System.in);
        String tahunInput = userOption.nextLine();

        while(!tahunValid) {
            try {
                Year.parse(tahunInput);
                tahunValid = true;
            } catch (Exception e) {
                System.err.println("\nTahun nya salah!\nSilahkan Masukkan Dengan Format (YYYY)");
                System.out.print("Masukkan tahun terbit lagi: ");
                tahunValid = false;
                tahunInput = userOption.nextLine();
            }
        }
        return tahunInput;
    }

    private static boolean cekBukuDiDatabase(String[] keywords, boolean isDisplay) throws IOException{

        // Membuka file pada database
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        // Membaca file pada database
        String dataFile = bufferInput.readLine();

        // Jika data pertama kosong maka false
        boolean isExist = false;
        int nomorData = 0;

        // Jika cari data, maka ini ditampilkan
        if (isDisplay) {
            System.out.println("\n| No |\tTahun |\tPenulis                |\tJudul Buku             |\tPenerbit");
            System.out.println("----------------------------------------------------------------------------------------------------------");
        }

        while(dataFile != null){

            // cek keywords di dalam baris
            isExist = true;

            for(String keyword:keywords) {
                isExist = isExist && dataFile.toLowerCase().contains(keyword.toLowerCase());
            }

            // jika keyword nya cocok, tampilkan
            if(isExist){
                if(isDisplay) {
                    nomorData++;
                    StringTokenizer stringToken = new StringTokenizer(dataFile, ",");
                    stringToken.nextToken();

                    System.out.printf("| %2d ", nomorData);
                    System.out.printf("|\t%4s  ", stringToken.nextToken());
                    System.out.printf("|\t%-20s   ", stringToken.nextToken());
                    System.out.printf("|\t%-20s   ", stringToken.nextToken());
                    System.out.printf("|\t%s   ", stringToken.nextToken());
                    System.out.print("\n");
                }else{
                    break;
                }
            }

            dataFile = bufferInput.readLine();
        }

        // Jika cari ada, maka ini ditampilkan
        if(isDisplay){
            System.out.println("----------------------------------------------------------------------------------------------------------");
        }
        return isExist;
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
