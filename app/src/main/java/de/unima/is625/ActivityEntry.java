package de.unima.is625;


public class ActivityEntry {

        private String act_name;
        private String act_location;
        private int act_range;
        private long id;


        public ActivityEntry(String act_name, String act_location, int act_range, long id) {
            this.act_name = act_name;
            this.act_location = act_location;
            this.act_range = act_range;
            this.id = id;
        }

        public String getAct_name() {
            return act_name;
        }

        public void setAct_name(String act_name) {
            this.act_name = act_name;
        }

        public String getAct_location() {
            return act_location;
        }

        public void setAct_location(String act_name) {
            this.act_name = act_location;
        }

        public int getAct_range() {
            return act_range;
        }

        public void setAct_range(int act_range) {
            this.act_range = act_range;
        }


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }


        @Override
        public String toString() {
            String output = act_name + " " + act_location + " " + act_range;

            return output;
        }
    }
