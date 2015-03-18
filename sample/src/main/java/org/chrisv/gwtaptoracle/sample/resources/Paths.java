package org.chrisv.gwtaptoracle.sample.resources;

public class Paths {
    public static class BeesPaths {
        public static final String BEES = "/bees";

        public static class BeePaths {
            public static final String BEE = "/{" + BeeParams.BEE_ID + "}";

            public static class BeeParams {
                public static final String BEE_ID = "bee-id";
            }
        }
    }
}
