public class Body {
    public static final double gravitationalConstant = 6.67e-11;

    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;

    public Body(double xP, double yP, double xV,
                double yV, double M, String img) {
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = M;
        imgFileName = img;
    }

    public Body(Body b) {
        xxPos = b.xxPos;
        yyPos = b.yyPos;
        xxVel = b.xxVel;
        yyVel = b.yyVel;
        mass = b.mass;
        imgFileName = b.imgFileName;
    }

    public double calcDistance(Body other) {
        double deltaX = Math.pow(Math.abs(xxPos - other.xxPos), 2);
        double deltaY = Math.pow(Math.abs(yyPos - other.yyPos), 2);
        return Math.sqrt(deltaX + deltaY);
    }

    public double calcForceExertedBy(Body other) {
        double r = calcDistance(other);
        double rSquared = r * r;
        return ((Body.gravitationalConstant * mass * other.mass) / rSquared);
    }

    public double calcForceExertedByX(Body other) {
        double r = calcDistance(other);
        double force = calcForceExertedBy(other);
        double deltaX = other.xxPos - xxPos;
        return (force * deltaX) / r;
    }

    public double calcForceExertedByY(Body other) {
        double r = calcDistance(other);
        double force = calcForceExertedBy(other);
        double deltaY = other.yyPos - yyPos;
        return (force * deltaY) / r;
    }

    public double calcNetForceExertedByX(Body[] bodies) {
        double totalForceInX = 0;
        for (Body b : bodies) {
            if (!b.equals(this)) {
                totalForceInX += calcForceExertedByX(b);
            }
        }
        return totalForceInX;
    }

    public double calcNetForceExertedByY(Body[] bodies) {
        double totalForceInY = 0;
        for (Body b : bodies) {
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
}
