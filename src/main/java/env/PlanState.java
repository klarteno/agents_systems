package env;

public enum PlanState {

    planAgentToBox {
        public boolean dependencyPath;
        @Override
        public PlanState nextState() {
            if(dependencyPath){
                return dependencyPathHasDependencies;
            }
            return getAgentToBox;
        }
        @Override
        public String responsiblePerson() {
            return "Employee";
        }
    },
    dependencyPathHasDependencies {
        @Override
        public PlanState nextState() {
            return Approved;
        }

        @Override
        public String responsiblePerson() {
            return "Team Leader";
        }
    },

    getAgentToBox {
        @Override
        public PlanState nextState() {
            return Approved;
        }

        @Override
        public String responsiblePerson() {
            return "Team Leader";
        }
    },


    Approved {
        @Override
        public PlanState nextState() {
            return this;
        }

        @Override
        public String responsiblePerson() {
            return "Department Manager";
        }
    };

    private boolean dependencyPath;

    public abstract PlanState nextState();
    public abstract String responsiblePerson();
}