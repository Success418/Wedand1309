package co.za.wedwise.Models;

/**
 * Created by balqisstudio on 3/24/2019.
 */

public class CityModels {

        private String CityId;
        private String CityName;
        private String CityImage;

        public String getCityId() {
            return CityId;
        }

        public void setCityId(String id) {
            this.CityId = id;
        }


        public String getCityName() {
            return CityName;
        }

        public void setCityName(String name) {
            this.CityName = name;
        }

        public String getCityImage() {
            return CityImage;

        }

        public void setCityImage(String image) {
            this.CityImage = image;
        }

    }
