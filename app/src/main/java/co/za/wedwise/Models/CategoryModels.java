package co.za.wedwise.Models;

/**
 * Created by balqisstudio on 3/24/2019.
 */

public class CategoryModels {

        private String CategoryId;
        private String CategoryName;
        private String CategoryImage;

        public String getCategoryId() {
            return CategoryId;
        }

        public void setCategoryId(String id) {
            this.CategoryId = id;
        }


        public String getCategoryName() {
            return CategoryName;
        }

        public void setCategoryName(String name) {
            this.CategoryName = name;
        }

        public String getCategoryImage() {
            return CategoryImage;

        }

        public void setCategoryImage(String image) {
            this.CategoryImage = image;
        }

    }
