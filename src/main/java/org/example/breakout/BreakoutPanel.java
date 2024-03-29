package org.example.breakout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class BreakoutPanel extends JPanel implements ActionListener, KeyListener {
	
	static final long serialVersionUID = 2L;

	private boolean gameRunning = true;
	private int livesLeft;
	private String screenMessage = "";
	private Ball ball;
	private Paddle paddle;
	private Brick bricks[];
	private JButton resetButton;

	public boolean isGameRunning() {
		return gameRunning;
	}

	public int getLivesLeft() {
		return livesLeft;
	}

	public String getScreenMessage() {
		return screenMessage;
	}

	public Ball getBall() {
		return ball;
	}

	public Paddle getPaddle() {
		return paddle;
	}

	public Brick[] getBricks() {
		return bricks;
	}

	public JButton getResetButton() {
		return resetButton;
	}

	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}

	public void setLivesLeft(int livesLeft) {
		this.livesLeft = livesLeft;
	}

	public void setScreenMessage(String screenMessage) {
		this.screenMessage = screenMessage;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}

	public void setPaddle(Paddle paddle) {
		this.paddle = paddle;
	}

	public void setBricks(Brick[] bricks) {
		this.bricks = bricks;
	}

	public void setResetButton(JButton resetButton) {
		this.resetButton = resetButton;
	}

	public BreakoutPanel(Breakout game) {
		
		addKeyListener(this);
		setFocusable(true);
		
		resetButton = new JButton();
		resetButton.setText("Restart Game");
		resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Init();
				resetButton.setVisible(false);
			}
		}); 
		resetButton.setPreferredSize(new Dimension(100,20));
		//resetButton.setLocation();
		resetButton.setVisible(false);
		
		this.add(resetButton);
		Timer timer = new Timer(5, this);
		timer.start();
		
		Init();
	}
	
	private void Init() {
		gameRunning = true;
		livesLeft = Settings.MAX_LIVES;
		screenMessage = "";
		// TODO: Create a new ball object and assign it to the appropriate variable
		ball = new Ball();
		// TODO: Create a new paddle object and assign it to the appropriate variable
		paddle = new Paddle();
		// TODO: Create a new bricks array (Use Settings.TOTAL_BRICKS)
		bricks = new Brick[Settings.TOTAL_BRICKS];
		// TODO: Call the createBricks() method
		createBricks(); 
	}
	
	private void createBricks() {
		int counter = 0;
		int x_space = 0;
		int y_space = 0;
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 5; y++) {
				bricks[counter] = new Brick((x * Settings.BRICK_WIDTH) + Settings.BRICK_HORI_PADDING + x_space, (y * Settings.BRICK_HEIGHT) + Settings.BRICK_VERT_PADDING + y_space);
				counter++;
				y_space++;
			}
			x_space++;
			y_space = 0;
		}
	}
	
	private void paintBricks(Graphics g) {
		// TODO: Loop through the bricks and call the paint() method
		for(int x = 0; x < bricks.length; x++)
		{
			bricks[x].paint(g);
		}
	}
	
	public void update() {
		if(gameRunning) {
			// TODO: Update the ball and paddle
			ball.update();
			paddle.update();
			collisions();
			repaint();
		}
	}
	
	private void gameOver() {
		// TODO: Set screen message
		screenMessage = "Game over";
		stopGame();
	}
	
	private void gameWon() {
		// TODO: Set screen message
		screenMessage = "You win!";
		stopGame();
	}
	
	private void stopGame() {
		gameRunning = false;
		resetButton.setVisible(true);
	}
	
	private void collisions() {
		// Check for loss
		if(ball.y > 450) {
			// Game over
			livesLeft--;
			if(livesLeft <= 0) {
				gameOver();
				return;
			} else {
				ball.resetPosition();
				ball.setYVelocity(-1);
			}
		}
		
		// Check for win
		boolean bricksLeft = false;
		for(int i = 0; i < bricks.length; i++) {
			// Check if there are any bricks left
			if(!bricks[i].isBroken()) {
				// Brick was found, close loop
				bricksLeft = true;
				break;
			}
		}
		if(!bricksLeft) {
			gameWon();
			return;
		}
		
		// Check collisions
		if(ball.getRectangle().intersects(paddle.getRectangle())) {
			// Simplified touching of paddle
			// Proper game would change angle of ball depending on where it hit the paddle
			ball.setYVelocity(-1);
		}
		
		for(int i = 0; i < bricks.length; i++) {
			if (ball.getRectangle().intersects(bricks[i].getRectangle())) {
				int ballLeft = (int) ball.getRectangle().getMinX();
	            int ballHeight = (int) ball.getRectangle().getHeight();
	            int ballWidth = (int) ball.getRectangle().getWidth();
	            int ballTop = (int) ball.getRectangle().getMinY();

	            Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
	            Point pointLeft = new Point(ballLeft - 1, ballTop);
	            Point pointTop = new Point(ballLeft, ballTop - 1);
	            Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

	            if (!bricks[i].isBroken()) {
	                if (bricks[i].getRectangle().contains(pointRight)) {
	                    ball.setXVelocity(-1);
	                } else if (bricks[i].getRectangle().contains(pointLeft)) {
	                    ball.setXVelocity(1);
	                }

	                if (bricks[i].getRectangle().contains(pointTop)) {
	                    ball.setYVelocity(1);
	                } else if (bricks[i].getRectangle().contains(pointBottom)) {
	                    ball.setYVelocity(-1);
	                }
	                bricks[i].setBroken(true);
	            }
			}
		}
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ball.paint(g);
        paddle.paint(g);
        paintBricks(g);
        
        // Draw lives left
        // TODO: Draw lives left in the top left hand corner
     	g.drawString("Lives: "+ livesLeft, (Settings.LIVES_POSITION_X), (Settings.LIVES_POSITION_Y));
        // Draw screen message
        if(screenMessage != null) {
        	g.setFont(new Font("Arial", Font.BOLD, 18));
        	int messageWidth = g.getFontMetrics().stringWidth(screenMessage);
        	g.drawString(screenMessage, (Settings.WINDOW_WIDTH / 2) - (messageWidth / 2), Settings.MESSAGE_POSITION);
        	
        }
    }

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO: Set the velocity of the paddle depending on whether the player is pressing left or right
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			paddle.setXVelocity(-1);
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			paddle.setXVelocity(1);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO: Set the velocity of the paddle after the player has released the keys
		if(e.getKeyLocation() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			paddle.setXVelocity(0);
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		update();
	}

	public java.util.List<Rectangle> getBrickRectangles() {
		java.util.List<Rectangle> brickRectangles = new ArrayList<>();
		for (Brick brick : bricks) {
			if (!brick.isBroken()) {
				brickRectangles.add(brick.getRectangle());
			}
		}
		return brickRectangles;
	}

}

