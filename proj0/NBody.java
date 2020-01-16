public class NBody {
    public static double readRadius(String txtFile) {
        In in = new In(txtFile);
        int parseFirst = in.readInt();
        return in.readDouble();
    }

    public static Body[] readBodies(String txtFile) {
        In in = new In(txtFile);
        Body[] bodies = new Body[5];
        int parseFirst = in.readInt();
        double parseSecond = in.readDouble();
        // I'm only doing this assuming we ALWAYS read 5 rows of data
        int counter = 0;
        while (counter < 5) {
            double xxPos = in.readDouble();
            double yyPos = in.readDouble();
            double xxVel = in.readDouble();
            double yyVel = in.readDouble();
            double mass = in.readDouble();
            String imgFileName = in.readString();
            bodies[counter] = new Body(xxPos, yyPos, xxVel, yyVel, mass, imgFileName);
            counter++;
        }
        return bodies;
    }
}
