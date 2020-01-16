public class NBody {
    public static void main(String[] args) {
        // Initializing
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double universeRadius = readRadius(filename);
        Body[] bodies = readBodies(filename);

        // Drawing the background
        StdDraw.setScale(-universeRadius, universeRadius);
        StdDraw.clear();
        StdDraw.picture(0, 0, "images/starfield.jpg");

        for (Body b : bodies) {
            b.draw();
        }
    }

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
