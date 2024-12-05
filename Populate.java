// Source code is decompiled from a .class file using FernFlower decompiler.
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Populate {
   private String file;
   private String connectionUrl;

   public Populate() {
   }

   public static void main(String[] var0) {
      Populate var1 = new Populate();
      var1.loadConfigAndPopulate();
   }

   public void loadConfigAndPopulate() {
      Properties var1 = new Properties();
      String var2 = "auth.cfg";

      try {
         FileInputStream var3 = new FileInputStream(var2);
         var1.load(var3);
         var3.close();
      } catch (FileNotFoundException var5) {
         System.out.println("Could not find config file.");
         System.exit(1);
      } catch (IOException var6) {
         System.out.println("Error reading config file.");
         System.exit(1);
      }

      String var7 = var1.getProperty("username");
      String var4 = var1.getProperty("password");
      if (var7 == null || var4 == null) {
         System.out.println("Username or password not provided.");
         System.exit(1);
      }

      this.connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;database=cs3380;user=" + var7 + ";password=" + var4 + ";encrypt=false;trustServerCertificate=false;loginTimeout=30;";
      this.race();
   }

   public void city() {
      String var1 = "INSERT INTO city (name, country) VALUES (?, ?)";
      this.file = "csv_files/city.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  while(true) {
                     String var5;
                     if ((var5 = var3.readLine()) == null) {
                        System.out.println("city table successfully populated");
                        break;
                     }

                     String[] var6 = var5.split(",");
                     String var7 = var6[1].trim();
                     String var8 = var6[2].trim();
                     var4.setString(1, var7);
                     var4.setString(2, var8);
                     var4.executeUpdate();
                  }
               } catch (Throwable var12) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var13) {
               try {
                  var3.close();
               } catch (Throwable var10) {
                  var13.addSuppressed(var10);
               }

               throw var13;
            }

            var3.close();
         } catch (Throwable var14) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var9) {
                  var14.addSuppressed(var9);
               }
            }

            throw var14;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var15) {
         var15.printStackTrace();
      } catch (FileNotFoundException var16) {
         System.out.println("csv file not found.");
      } catch (IOException var17) {
         System.out.println("Error reading csv file.");
      }

   }

   public void constructor() {
      String var1 = "INSERT INTO constructor (constructorRef, name, nationality) VALUES (?, ?, ?)";
      this.file = "csv_files/constructors.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  while(true) {
                     String var5;
                     if ((var5 = var3.readLine()) == null) {
                        System.out.println("constructor table successfully populated");
                        break;
                     }

                     String[] var6 = var5.split(",");
                     String var7 = var6[1].trim();
                     String var8 = var6[2].trim();
                     String var9 = var6[3].trim();
                     var4.setString(1, var7);
                     var4.setString(2, var8);
                     var4.setString(3, var9);
                     var4.executeUpdate();
                  }
               } catch (Throwable var13) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                     }
                  }

                  throw var13;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var14) {
               try {
                  var3.close();
               } catch (Throwable var11) {
                  var14.addSuppressed(var11);
               }

               throw var14;
            }

            var3.close();
         } catch (Throwable var15) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var10) {
                  var15.addSuppressed(var10);
               }
            }

            throw var15;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var16) {
         var16.printStackTrace();
      } catch (FileNotFoundException var17) {
         System.out.println("csv file not found.");
      } catch (IOException var18) {
         System.out.println("Error reading csv file.");
      }

   }

   public void constructorResult() {
      String var1 = "INSERT INTO constructorResults (raceID,constructorID,points,status) VALUES (?, ?,?,?)";
      this.file = "csv_files/constructor_results.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  while(true) {
                     String var5;
                     if ((var5 = var3.readLine()) == null) {
                        System.out.println("constructor_Results table successfully populated");
                        break;
                     }

                     String[] var6 = var5.split(",");
                     int var7 = Integer.parseInt(var6[1].trim());
                     int var8 = Integer.parseInt(var6[2].trim());
                     int var9 = Integer.parseInt(var6[3].trim());
                     String var10 = var6[4].trim();
                     var4.setInt(1, var7);
                     var4.setInt(2, var8);
                     var4.setInt(3, var9);
                     if (!var10.equals("\\N") && !var10.isEmpty()) {
                        var4.setString(4, var10);
                     } else {
                        var4.setNull(4, 12);
                     }

                     var4.executeUpdate();
                  }
               } catch (Throwable var14) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var15) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var15.addSuppressed(var12);
               }

               throw var15;
            }

            var3.close();
         } catch (Throwable var16) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var16.addSuppressed(var11);
               }
            }

            throw var16;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var17) {
         var17.printStackTrace();
      } catch (FileNotFoundException var18) {
         System.out.println("csv file not found.");
      } catch (IOException var19) {
         System.out.println("Error reading csv file.");
      }

   }

   public void constructorStanding() {
      String var1 = "INSERT INTO constructorStanding (raceID,wins,points,position,constructorID) VALUES (?, ?,?,?,?)";
      this.file = "csv_files/constructor_standings.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  while(true) {
                     String var5;
                     if ((var5 = var3.readLine()) == null) {
                        System.out.println("constructor_Results table successfully populated");
                        break;
                     }

                     String[] var6 = var5.split(",");
                     int var7 = Integer.parseInt(var6[1].trim());
                     int var8 = Integer.parseInt(var6[2].trim());
                     int var9 = Integer.parseInt(var6[3].trim());
                     int var10 = Integer.parseInt(var6[4].trim());
                     int var11 = Integer.parseInt(var6[5].trim());
                     var4.setInt(1, var7);
                     var4.setInt(2, var8);
                     var4.setInt(3, var9);
                     var4.setInt(4, var10);
                     var4.setInt(5, var11);
                     var4.executeUpdate();
                  }
               } catch (Throwable var15) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                     }
                  }

                  throw var15;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var16) {
               try {
                  var3.close();
               } catch (Throwable var13) {
                  var16.addSuppressed(var13);
               }

               throw var16;
            }

            var3.close();
         } catch (Throwable var17) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var12) {
                  var17.addSuppressed(var12);
               }
            }

            throw var17;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var18) {
         var18.printStackTrace();
      } catch (FileNotFoundException var19) {
         System.out.println("csv file not found.");
      } catch (IOException var20) {
         System.out.println("Error reading csv file.");
      }

   }

   public void qualifyingRecord() {
      String var1 = "INSERT INTO qualifyingRecord (raceID, driverID, constructorID, number, position, q1, q2, q3) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      this.file = "csv_files/qualifying.csv";
      System.out.println("tryign");

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  while(true) {
                     String var5;
                     if ((var5 = var3.readLine()) == null) {
                        System.out.println("qualifyingRecord table successfully populated");
                        break;
                     }

                     String[] var6 = var5.split(",");
                     int var7 = Integer.parseInt(var6[1].trim());
                     int var8 = Integer.parseInt(var6[2].trim());
                     int var9 = Integer.parseInt(var6[3].trim());
                     int var10 = Integer.parseInt(var6[4].trim());
                     int var11 = Integer.parseInt(var6[5].trim());
                     Double var12 = var6[6].trim().equals("\\N") ? null : this.parseTimeToDecimal(var6[6].trim());
                     Double var13 = var6[7].trim().equals("\\N") ? null : this.parseTimeToDecimal(var6[7].trim());
                     Double var14 = var6[8].trim().equals("\\N") ? null : this.parseTimeToDecimal(var6[8].trim());
                     var4.setInt(1, var7);
                     var4.setInt(2, var8);
                     var4.setInt(3, var9);
                     var4.setInt(4, var10);
                     var4.setInt(5, var11);
                     if (var12 == null) {
                        var4.setNull(6, 3);
                     } else {
                        var4.setDouble(6, var12);
                     }

                     if (var13 == null) {
                        var4.setNull(7, 3);
                     } else {
                        var4.setDouble(7, var13);
                     }

                     if (var14 == null) {
                        var4.setNull(8, 3);
                     } else {
                        var4.setDouble(8, var14);
                     }

                     var4.executeUpdate();
                     System.out.println("tryign");
                  }
               } catch (Throwable var18) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }
                  }

                  throw var18;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var19) {
               try {
                  var3.close();
               } catch (Throwable var16) {
                  var19.addSuppressed(var16);
               }

               throw var19;
            }

            var3.close();
         } catch (Throwable var20) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var15) {
                  var20.addSuppressed(var15);
               }
            }

            throw var20;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var21) {
         var21.printStackTrace();
      } catch (FileNotFoundException var22) {
         System.out.println("csv file not found.");
      } catch (IOException var23) {
         System.out.println("Error reading csv file.");
      }

   }

   private Double parseTimeToDecimal(String var1) {
      try {
         String[] var2 = var1.split(":");
         double var3 = Double.parseDouble(var2[0]) * 60.0;
         double var5 = Double.parseDouble(var2[1]);
         return var3 + var5;
      } catch (Exception var7) {
         throw new IllegalArgumentException("Invalid time format: " + var1, var7);
      }
   }

   public void circuit() {
      String var1 = "INSERT INTO circuit (cityID, circuitRef, name, long, lat, altitude) VALUES (?, ?, ?, ?, ?, ?)";
      this.file = "csv_files/circuits.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  while(true) {
                     String var5;
                     if ((var5 = var3.readLine()) == null) {
                        System.out.println("Circuit table successfully populated!");
                        break;
                     }

                     String[] var6 = var5.split(",");
                     int var7 = Integer.parseInt(var6[1].trim());
                     String var8 = var6[2].trim();
                     String var9 = var6[3].trim();
                     double var10 = Double.parseDouble(var6[6].trim());
                     double var12 = Double.parseDouble(var6[7].trim());
                     int var14 = Integer.parseInt(var6[8].trim());
                     var4.setInt(1, var7);
                     var4.setString(2, var8);
                     var4.setString(3, var9);
                     var4.setDouble(4, var10);
                     var4.setDouble(5, var12);
                     var4.setInt(6, var14);
                     var4.executeUpdate();
                  }
               } catch (Throwable var18) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }
                  }

                  throw var18;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var19) {
               try {
                  var3.close();
               } catch (Throwable var16) {
                  var19.addSuppressed(var16);
               }

               throw var19;
            }

            var3.close();
         } catch (Throwable var20) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var15) {
                  var20.addSuppressed(var15);
               }
            }

            throw var20;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var21) {
         var21.printStackTrace();
      } catch (FileNotFoundException var22) {
         System.out.println("csv file not found.");
      } catch (IOException var23) {
         System.out.println("Error reading csv file.");
      }

   }

   public void race() {
      String var1 = "INSERT INTO race (year, round, circuitID, name, date, time) VALUES (?, ?, ?, ?, ?, ?)";
      this.file = "csv_files/races.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();
                  int var6 = 0;

                  String var5;
                  while((var5 = var3.readLine()) != null) {
                     String[] var7 = var5.split(",");
                     int var8 = Integer.parseInt(var7[1].trim());
                     int var9 = Integer.parseInt(var7[2].trim());
                     int var10 = Integer.parseInt(var7[3].trim());
                     String var11 = var7[4].trim();
                     String var12 = var7[5].trim();
                     String var13 = var7[6].trim();
                     var4.setInt(1, var8);
                     var4.setInt(2, var9);
                     var4.setInt(3, var10);
                     var4.setString(4, var11);
                     Date var14 = this.parseDate(var12);
                     Time var15 = this.parseTime(var13);
                     if (var14 != null) {
                        var4.setDate(5, var14);
                     } else {
                        var4.setNull(5, 91);
                     }

                     if (var15 != null) {
                        var4.setTime(6, var15);
                     } else {
                        var4.setNull(6, 92);
                     }

                     var4.executeUpdate();
                     ++var6;
                     System.out.println("" + var6 + "imserted");
                     System.out.println(var10);
                  }

                  System.out.println("races table successfully populated");
               } catch (Throwable var19) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var18) {
                        var19.addSuppressed(var18);
                     }
                  }

                  throw var19;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var20) {
               try {
                  var3.close();
               } catch (Throwable var17) {
                  var20.addSuppressed(var17);
               }

               throw var20;
            }

            var3.close();
         } catch (Throwable var21) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var16) {
                  var21.addSuppressed(var16);
               }
            }

            throw var21;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var22) {
         var22.printStackTrace();
      } catch (FileNotFoundException var23) {
         System.out.println("csv file not found.");
      } catch (IOException var24) {
         System.out.println("Error reading csv file.");
      }

   }

   private Date parseDate(String var1) {
      try {
         var1 = var1.replace("\"", "").trim();
         return !var1.equals("\\N") && !var1.isEmpty() ? Date.valueOf(var1) : null;
      } catch (IllegalArgumentException var3) {
         throw new IllegalArgumentException("Invalid date format: " + var1, var3);
      }
   }

   private Time parseTime(String var1) {
      try {
         var1 = var1.replace("\"", "").trim();
         return !var1.equals("\\N") && !var1.isEmpty() ? Time.valueOf(var1) : null;
      } catch (IllegalArgumentException var3) {
         throw new IllegalArgumentException("Invalid time format: " + var1, var3);
      }
   }

   public void sprintResult() {
      String var1 = "INSERT INTO sprintResult (raceID, driverID, constructorID, number, grid, position, positionOrder, points, laps, time, milliseconds, fastestLap, fastestLapTime, statusID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      this.file = "csv_files/sprint_results.csv";

      try {
         Connection var2 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var3 = new BufferedReader(new FileReader(this.file));

            try {
               PreparedStatement var4 = var2.prepareStatement(var1);

               try {
                  var3.readLine();

                  String var5;
                  while((var5 = var3.readLine()) != null) {
                     String[] var6 = var5.split(",");
                     int var7 = Integer.parseInt(var6[1].trim());
                     int var8 = Integer.parseInt(var6[2].trim());
                     int var9 = Integer.parseInt(var6[3].trim());
                     int var10 = Integer.parseInt(var6[4].trim());
                     int var11 = Integer.parseInt(var6[5].trim());
                     Integer var12 = var6[6].trim().equals("\\N") ? null : Integer.parseInt(var6[6].trim());
                     int var13 = Integer.parseInt(var6[8].trim());
                     int var14 = Integer.parseInt(var6[9].trim());
                     int var15 = Integer.parseInt(var6[10].trim());
                     String var16 = var6[11].trim().equals("\\N") ? null : var6[10].trim();
                     Integer var17 = var6[12].trim().equals("\\N") ? null : Integer.parseInt(var6[12].trim());
                     Integer var18 = var6[13].trim().equals("\\N") ? null : Integer.parseInt(var6[13].trim());
                     String var19 = var6[14].trim().equals("\\N") ? null : var6[14].trim();
                     int var20 = Integer.parseInt(var6[15].trim());
                     var4.setInt(1, var7);
                     var4.setInt(2, var8);
                     var4.setInt(3, var9);
                     var4.setInt(4, var10);
                     var4.setInt(5, var11);
                     if (var12 != null) {
                        var4.setInt(6, var12);
                     } else {
                        var4.setNull(6, 4);
                     }

                     var4.setInt(7, var13);
                     var4.setInt(8, var14);
                     var4.setInt(9, var15);
                     if (var16 != null) {
                        var4.setString(10, var16);
                     } else {
                        var4.setNull(10, 12);
                     }

                     if (var17 != null) {
                        var4.setInt(11, var17);
                     } else {
                        var4.setNull(11, 4);
                     }

                     if (var18 != null) {
                        var4.setInt(12, var18);
                     } else {
                        var4.setNull(12, 4);
                     }

                     if (var19 != null) {
                        var4.setString(13, var19);
                     } else {
                        var4.setNull(13, 12);
                     }

                     var4.setInt(14, var20);
                     var4.executeUpdate();
                  }

                  System.out.println("sprintResult table successfully populated");
               } catch (Throwable var24) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var23) {
                        var24.addSuppressed(var23);
                     }
                  }

                  throw var24;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var25) {
               try {
                  var3.close();
               } catch (Throwable var22) {
                  var25.addSuppressed(var22);
               }

               throw var25;
            }

            var3.close();
         } catch (Throwable var26) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var21) {
                  var26.addSuppressed(var21);
               }
            }

            throw var26;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var27) {
         var27.printStackTrace();
      } catch (FileNotFoundException var28) {
         System.out.println("CSV file not found.");
      } catch (IOException var29) {
         System.out.println("Error reading CSV file.");
      }

   }

   private void driver() {
      try {
         Connection var1 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var2 = new BufferedReader(new FileReader("csv_files/drivers.csv"));
            var2.readLine();

            while(true) {
               String var3;
               if ((var3 = var2.readLine()) == null) {
                  var2.close();
                  System.out.println("driver table successfully populated");
                  break;
               }

               String[] var4 = var3.split(",");
               PreparedStatement var5 = var1.prepareStatement("INSERT INTO driver (driverRef, forename, surname,number,nationality, code, dob) VALUES (?, ?, ?, ?,?,?,?)");
               var5.setString(1, var4[1].trim());
               var5.setString(2, var4[4].trim());
               var5.setString(3, var4[5].trim());
               Integer var6 = var4[2].trim().equals("\\N") ? null : Integer.parseInt(var4[2].trim());
               if (var6 != null) {
                  var5.setInt(4, var6);
               } else {
                  var5.setNull(4, 4);
               }

               var5.setString(5, var4[7].trim());
               var5.setString(6, var4[3].trim());
               var5.setDate(7, Date.valueOf(var4[6]));
               var5.executeUpdate();
               var5.close();
            }
         } catch (Throwable var8) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (SQLException | IOException var9) {
         var9.printStackTrace();
      }

   }

   private void status() {
      try {
         Connection var1 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var2 = new BufferedReader(new FileReader("csv_files/status.csv"));
            var2.readLine();

            while(true) {
               String var3;
               if ((var3 = var2.readLine()) == null) {
                  var2.close();
                  break;
               }

               String[] var4 = var3.split(",");
               PreparedStatement var5 = var1.prepareStatement("INSERT INTO status (status) VALUES (?)");
               var5.setString(1, var4[1]);
               var5.executeUpdate();
               var5.close();
            }
         } catch (Throwable var7) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (SQLException | IOException var8) {
         var8.printStackTrace();
      }

   }

   private String parseTime1(String var1) throws ParseException {
      var1 = var1.replace("\"", "").trim();
      SimpleDateFormat var2 = new SimpleDateFormat("m:ss.SSS");
      SimpleDateFormat var3 = new SimpleDateFormat("HH:mm:ss.SSS");
      return var3.format(var2.parse(var1));
   }

   private void LapTime() {
      try {
         Connection var1 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var2 = new BufferedReader(new FileReader("csv_files/lap_times.csv"));
            var2.readLine();

            while(true) {
               String var3;
               if ((var3 = var2.readLine()) == null) {
                  var2.close();
                  break;
               }

               String[] var4 = var3.split(",");
               PreparedStatement var5 = var1.prepareStatement("INSERT INTO laptime (raceID, driverID, lap, position, time, milliseconds) VALUES (?, ?, ?, ?, ?, ?)");
               var5.setInt(1, Integer.parseInt(var4[0].trim()));
               var5.setInt(2, Integer.parseInt(var4[1].trim()));
               var5.setInt(3, Integer.parseInt(var4[2].trim()));
               var5.setInt(4, Integer.parseInt(var4[3].trim()));
               String var6 = this.parseTime1(var4[4].trim()).replace("00:", "");
               var5.setString(5, var6);
               var5.setInt(6, Integer.parseInt(var4[5].trim()));
               var5.executeUpdate();
               var5.close();
            }
         } catch (Throwable var8) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (SQLException | ParseException | IOException var9) {
         var9.printStackTrace();
      }

   }

   public static String parseTime2(String var0) throws ParseException {
      try {
         var0 = var0.replace("\"", "").trim();
         DateTimeFormatter var1 = DateTimeFormatter.ofPattern("HH:mm:ss");
         LocalTime var2 = LocalTime.parse(var0, var1);
         return var2.toString();
      } catch (Exception var3) {
         throw new ParseException("Unable to parse time: " + var0, 0);
      }
   }

   public static String parseTime3(String var0) throws ParseException {
      try {
         var0 = var0.replace("\"", "").trim();
         String[] var1;
         int var2;
         if (var0.contains(":")) {
            var1 = var0.split(":");
            var2 = Integer.parseInt(var1[0]);
            String[] var8 = var1[1].split("\\.");
            int var9 = Integer.parseInt(var8[0]);
            int var5 = Integer.parseInt(var8[1]);
            LocalTime var6 = LocalTime.of(0, var2, var9, var5 * 1000000);
            return var6.toString();
         } else {
            var1 = var0.split("\\.");
            var2 = Integer.parseInt(var1[0]);
            int var3 = Integer.parseInt(var1[1]);
            LocalTime var4 = LocalTime.of(0, 0, var2, var3 * 1000000);
            return var4.toString();
         }
      } catch (Exception var7) {
         throw new ParseException("Invalid duration format: " + var0, 0);
      }
   }

   private void Pitstop() {
      try {
         Connection var1 = DriverManager.getConnection(this.connectionUrl);

         try {
            BufferedReader var2 = new BufferedReader(new FileReader("csv_files/pit_stops.csv"));
            var2.readLine();

            while(true) {
               String var3;
               if ((var3 = var2.readLine()) == null) {
                  var2.close();
                  System.out.println("pitstop table successfully populated");
                  break;
               }

               String[] var4 = var3.split(",");
               PreparedStatement var5 = var1.prepareStatement("INSERT INTO pitstop (raceID, driverID, stop, lap, time, duration, milliseconds) VALUES (?, ?, ?, ?, ?, ?, ?)");
               var5.setInt(1, Integer.parseInt(var4[0].trim()));
               var5.setInt(2, Integer.parseInt(var4[1].trim()));
               var5.setInt(3, Integer.parseInt(var4[2].trim()));
               var5.setInt(4, Integer.parseInt(var4[3].trim()));
               String var6 = parseTime2(var4[4].trim());
               var5.setString(5, var6);
               String var7 = parseTime3(var4[5].trim());
               var5.setString(6, var7.replace("00:", ""));
               var5.setInt(7, Integer.parseInt(var4[6].trim()));
               var5.executeUpdate();
               var5.close();
            }
         } catch (Throwable var9) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (SQLException | ParseException | IOException var10) {
         var10.printStackTrace();
      }

   }
}
