public class NBody {
    public static void main(String[] args) {
        // Initializing
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double universeRadius = readRadius(filename);
        Body[] bodies = readBodies(filename);

        // Drawing the background
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-universeRadius, universeRadius);

        // Animating the "Universe"
        double time = 0;
        while (time <= T) {
            // Instantiate arrays
            double[] xForces = new double[5];
            double[] yForces = new double[5];
            // Get the xForces
            for (int i = 0; i < 5; i++) {
                xForces[i] = bodies[i].calcNetForceExertedByX(bodies);
            }
            // Get the yForces
            for (int y = 0; y < 5; y++) {
                yForces[y] = bodies[y].calcNetForceExertedByY(bodies);
            }
            // Update the positions for each body
            for (int z = 0; z < 5; z++) {
                bodies[z].update(dt, xForces[z], yForces[z]);
            }
            // Draw the background image
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/starfield.jpg");
            // Draw all of the bodies
            for (Body b : bodies) {
                b.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            // Iterate time
            time += dt;
        }
    }

    public static double readRadius(String txtFile) {
        In in = new In(txtFile);
        in.readInt();
        return in.readDouble();
    }

    public static Body[] readBodies(String txtFile) {
        In in = new In(txtFile);
        Body[] bodies = new Body[5];
        in.readInt();
        in.readDouble();
        int counter = 0;
        while (!in.isEmpty()) {
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
