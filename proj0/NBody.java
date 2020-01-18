public class NBody {
    public static void main(String[] args) {
        // Initializing
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double radius = readRadius(filename);
        Planet[] planets = readBodies(filename);

        // Drawing the background
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-radius, radius);

        // How many times to update
        int numPlanets = planets.length;

        // Animating the "Universe"
        double time = 0;
        while (time <= T) {
            // Instantiate arrays
            double[] xForces = new double[numPlanets];
            double[] yForces = new double[numPlanets];
            // Get the xForces
            for (int i = 0; i < numPlanets; i++) {
                xForces[i] = planets[i].calcNetForceExertedByX(planets);
            }
            // Get the yForces
            for (int y = 0; y < numPlanets; y++) {
                yForces[y] = planets[y].calcNetForceExertedByY(planets);
            }
            // Update the positions for each body
            for (int z = 0; z < numPlanets; z++) {
                planets[z].update(dt, xForces[z], yForces[z]);
            }
            // Draw the background image
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/starfield.jpg");
            // Draw all of the planets
            for (Planet b : planets) {
                b.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            // Iterate time
            time += dt;
        }
        StdOut.printf("%d\n", numPlanets);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < numPlanets; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                    planets[i].yyVel, planets[i].mass, planets[i].imgFileName);
        }
    }

    public static double readRadius(String txtFile) {
        In in = new In(txtFile);
        in.readInt();
        return in.readDouble();
    }

    public static Planet[] readBodies(String txtFile) {
        In in = new In(txtFile);
        int numberOfPlanets = in.readInt();
        Planet[] planets = new Planet[numberOfPlanets];
        in.readDouble();
        int counter = 0;
        while (counter < numberOfPlanets) {
            double xxPos = in.readDouble();
            double yyPos = in.readDouble();
            double xxVel = in.readDouble();
            double yyVel = in.readDouble();
            double mass = in.readDouble();
            String imgFileName = in.readString();
            planets[counter] = new Planet(xxPos, yyPos, xxVel, yyVel, mass, imgFileName);
            counter++;
        }
        return planets;
    }
}
