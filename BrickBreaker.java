import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
public class BrickBreaker extends JFrame implements KeyListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private final int PADDLE_WIDTH = 100;
    private final int PADDLE_HEIGHT = 20;
    private final int BALL_SIZE = 20;
    private final int BRICK_WIDTH = 50;
    private final int BRICK_HEIGHT = 20;
    private final int NUM_ROWS = 5;
    private final int NUM_COLS = 10;

    private boolean isPlaying = false;
    private boolean isGameOver = false;
    private boolean isGameWon = false;

    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private int paddleY = HEIGHT - 50;
    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT - 100;
    private int ballDX = 3;
    private int ballDY = -3;

    private List<Brick> bricks;

    public BrickBreaker() {
        setTitle("Brick Breaker");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);

        bricks = new ArrayList<>();
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                bricks.add(new Brick(col * BRICK_WIDTH, row * BRICK_HEIGHT));
            }
        }

        Timer timer = new Timer(10, e -> {
            if (isPlaying && !isGameOver && !isGameWon) {
                moveBall();
                checkCollisions();
                repaint();
            }
        });
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw paddle
        g2d.setColor(Color.BLUE);
        g2d.fillRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g2d.setColor(Color.RED);
        g2d.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        // Draw bricks
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                g2d.setColor(Color.GREEN);
                g2d.fillRect(brick.getX(), brick.getY(), BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        // Display game over or game won message
        if (isGameOver) {
            g2d.setColor(Color.RED);
            g2d.drawString("Game Over", WIDTH / 2 - 40, HEIGHT / 2);
        } else if (isGameWon) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("You Win!", WIDTH / 2 - 30, HEIGHT / 2);
        }
    }

    private void moveBall() {
        ballX += ballDX;
        ballY += ballDY;
        if (ballX <= 0 || ballX >= WIDTH - BALL_SIZE) {
            ballDX = -ballDX;
        }
        if (ballY <= 0) {
            ballDY = -ballDY;
        }
        if (ballY >= HEIGHT - BALL_SIZE) {
            isGameOver = true;
        }
    }

    private void checkCollisions() {
        Rectangle ballRect = new Rectangle(ballX, ballY, BALL_SIZE, BALL_SIZE);
        Rectangle paddleRect = new Rectangle(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        if (ballRect.intersects(paddleRect)) {
            ballDY = -ballDY;
        }

        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                Rectangle brickRect = new Rectangle(brick.getX(), brick.getY(), BRICK_WIDTH, BRICK_HEIGHT);
                if (ballRect.intersects(brickRect)) {
                    ballDY = -ballDY;
                    brick.setVisible(false);
                    checkGameWon();
                    break;
                }
            }
        }
    }

    private void checkGameWon() {
        boolean allBricksDestroyed = true;
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                allBricksDestroyed = false;
                break;
            }
        }
        if (allBricksDestroyed) {
            isGameWon = true;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && paddleX > 0) {
            paddleX -= 10;
        }
        if (key == KeyEvent.VK_RIGHT && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += 10;
        }
        if (key == KeyEvent.VK_SPACE) {
            isPlaying = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BrickBreaker game = new BrickBreaker();
            game.setVisible(true);
        });
    }
}

class Brick {
    private int x;
    private int y;
    private boolean visible;

    public Brick(int x, int y) {
        this.x = x;
        this.y = y;
        this.visible = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}