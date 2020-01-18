public class Planet {
    public static final double gravitationalConstant = 6.67e-11;

    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;

    public Planet(double xP, double yP, double xV,
                  double yV, double M, String img) {
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = M;
        imgFileName = img;
    }

    public Planet(Planet b) {
        xxPos = b.xxPos;
        yyPos = b.yyPos;
        xxVel = b.xxVel;
        yyVel = b.yyVel;
        mass = b.mass;
        imgFileName = b.imgFileName;
    }

    public double calcDistance(Planet other) {
        double deltaX = Math.pow(Math.abs(xxPos - other.xxPos), 2);
        double deltaY = Math.pow(Math.abs(yyPos - other.yyPos), 2);
        return Math.sqrt(deltaX + deltaY);
    }

    public double calcForceExertedBy(Planet other) {
        double r = calcDistance(other);
        double rSquared = r * r;
        return ((Planet.gravitationalConstant * mass * other.mass) / rSquared);
    }

    public double calcForceExertedByX(Planet other) {
        double r = calcDistance(other);
        double force = calcForceExertedBy(other);
        double deltaX = other.xxPos - xxPos;
        return (force * deltaX) / r;
    }

    public double calcForceExertedByY(Planet other) {
        double r = calcDistance(other);
        double force = calcForceExertedBy(other);
        double deltaY = other.yyPos - yyPos;
        return (force * deltaY) / r;
    }

    public double calcNetForceExertedByX(Planet[] planets) {
        double totalForceInX = 0;
        for (Planet b : planets) {
            if (!b.equals(this)) {
                totalForceInX += calcForceExertedByX(b);
            }
        }
        return totalForceInX;
    }

    public double calcNetForceExertedByY(Planet[] planets) {
        double totalForceInY = 0;
        for (Planet b : planets) {
            if (!b.equals(this)) {
                totalForceInY += calcForceExertedByY(b);
            }
        }
        return totalForceInY;
    }

    public void update(double dt, double fX, double fY) {
        double aX = fX / mass;
        double aY = fY / mass;
        xxVel = xxVel + (dt * aX);
        yyVel = yyVel + (dt * aY);
        xxPos = xxPos + (dt * xxVel);
        yyPos = yyPos + (dt * yyVel);
    }

    public void draw() {
        StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
    }
}
