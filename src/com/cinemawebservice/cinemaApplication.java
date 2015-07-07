package com.cinemawebservice;
   import org.glassfish.jersey.server.ResourceConfig;


   public class cinemaApplication extends ResourceConfig {
      public cinemaApplication() {
          packages("com.cinemawebservice");
    }
   }