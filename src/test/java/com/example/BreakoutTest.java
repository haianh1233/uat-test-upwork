package com.example;

import org.assertj.swing.assertions.Assertions;
import org.assertj.swing.fixture.FrameFixture;
import org.example.breakout.*;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.event.KeyEvent;

public class BreakoutTest {

    @Test
    public void testBallBouncingAgainstWalls() throws InterruptedException {
        Breakout breakout = new Breakout();

        Ball ball = breakout.getPanel().getBall();

        int count = 0;
        int rightWallBounceCount = 0;
        int leftWallBounceCount = 0;
        int topWallBounceCount = 0;


        while (count < 100_000) {
            System.out.println(ball.getX());
            System.out.println(ball.getY());

            if (ball.getX() == (Settings.WINDOW_WIDTH - Settings.BALL_WIDTH - 15) && ball.getXVelocity() == -1) {
                System.out.println("Ball bounce with right wall");
                rightWallBounceCount++;
            }

            if (ball.getX() == 0 && ball.getXVelocity() == 1) {
                System.out.println("Ball bounce with left wall");
                leftWallBounceCount++;
            }

            if (ball.getY() == 0 && ball.getYVelocity() == 1) {
                System.out.println("Ball bounce with top wall");
                topWallBounceCount++;
            }

            if (rightWallBounceCount > 0 && leftWallBounceCount > 0 && topWallBounceCount > 0) {
                System.out.println("Stop the test");
                break;
            }

            count++;
            Thread.sleep(5);
        }

        Assert.assertTrue(rightWallBounceCount > 0);
        Assert.assertTrue(leftWallBounceCount > 0);
        Assert.assertTrue(topWallBounceCount > 0);
        Thread.sleep(3_000);
    }

    @Test
    public void testClickedRestartTheGameShouldRestart() throws InterruptedException {
        Breakout breakout = new Breakout();

        breakout.getPanel().setLivesLeft(0);
        breakout.getPanel().getBall().setY(451);
        breakout.getPanel().getBall().setYVelocity(1);
        breakout.getPanel().update();

        // Check the reset buttun is available
        Assert.assertTrue(breakout.getPanel().getResetButton().isVisible());

        breakout.getPanel().getResetButton().doClick();

        // Check the game is rs with lives = MAX_LIVES
        Assertions.assertThat(breakout.getPanel().getLivesLeft()).isEqualTo(Settings.MAX_LIVES);
        Thread.sleep(3_000);

    }

    @Test
    public void testWhenMovingArrowOnKeyBoardThePaddleMovesAsWell() throws InterruptedException, AWTException {
        Robot robot = new Robot();
        Breakout breakout = new Breakout();

        //CLick left arrow
        robot.keyPress(KeyEvent.VK_LEFT);
        robot.waitForIdle();
        System.out.println("paddle x: " + breakout.getPanel().getPaddle().getX());

        Assert.assertTrue(breakout.getPanel().getPaddle().getX() < Settings.INITIAL_PADDLE_X);


        //Reset position
        breakout.getPanel().getPaddle().resetPosition();

        //Send click right arrow
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.waitForIdle();
        robot.wait(3000);
        System.out.println("paddle x: " + breakout.getPanel().getPaddle().getX());

        Assert.assertTrue(breakout.getPanel().getPaddle().getX() > Settings.INITIAL_PADDLE_X);

    }

    @Test
    public void testWithoutCommandOnKeyboardThePaddleShouldNotMove() throws InterruptedException {
        Breakout breakout = new Breakout();

        // Leave the game run for 10s
        Thread.sleep(10_000);
        System.out.println("paddle x: " + breakout.getPanel().getPaddle().getX());

        Assert.assertEquals(Settings.INITIAL_PADDLE_X, breakout.getPanel().getPaddle().getX());
        Thread.sleep(3_000);

    }

    @Test
    public void testWhenBallTouchesTheBricksTheyDisappear() throws InterruptedException {
        Breakout breakout = new Breakout();
        BreakoutPanel panel = breakout.getPanel();

        // Simulate ball collision with a brick
        Ball ball = panel.getBall();
        Brick brick = panel.getBricks()[0]; // Assuming there is at least one brick
        ball.setX(brick.getX() + brick.getWidth() / 2);
        ball.setY(brick.getY() + brick.getHeight() / 2);


        // Check if the brick is initially not broken
        Assert.assertFalse(brick.isBroken());

        // Trigger the ball update to process the collision
        panel.update();

        // Check if the brick is broken after the collision
        Assert.assertTrue(brick.isBroken());

        // Check if the brick is no longer painted on the panel
        Assert.assertFalse(panel.getBrickRectangles().contains(brick.getRectangle()));
        Thread.sleep(3_000);
    }

    @Test
    public void testWhenAllBrickDisappearMessageShouldBeDisplayed() throws InterruptedException {
        Breakout breakout = new Breakout();
        BreakoutPanel panel = breakout.getPanel();

        // Break all bricks
        for (Brick brick : panel.getBricks()) {
            brick.setBroken(true);
        }

        // Run the game for a short duration
        Thread.sleep(5_000);

        // Assert that the screen message is displayed
        Assert.assertEquals("You win!", panel.getScreenMessage());
        Thread.sleep(3_000);

    }

    @Test
    public void testDisplayGameOverMessageWhenLivesAreZero() throws InterruptedException {
        Breakout breakout = new Breakout();

        breakout.getPanel().setLivesLeft(0);
        breakout.getPanel().getBall().setY(451);
        breakout.getPanel().getBall().setYVelocity(1);

        breakout.getPanel().update();

        Assertions.assertThat(breakout.getPanel().getScreenMessage()).isEqualTo("Game over");
    }

    @Test
    public void atTheStartOfTheGameBallShouldBeSittingAtPaddle() throws InterruptedException {
        Breakout breakout = new Breakout();
        BreakoutPanel panel = breakout.getPanel();
        Ball ball = panel.getBall();
        Paddle paddle = panel.getPaddle();

        // Assert that the ball's position is same as paddle
        // X
        Assert.assertTrue(ball.getX() >= paddle.getX());
        Assert.assertTrue(ball.getX() + ball.getWidth() <= paddle.getX() + paddle.getWidth());

        // Y
        Assert.assertTrue(ball.getY() + ball.getHeight() <= paddle.getY() + paddle.getHeight());
        Thread.sleep(3_000);
    }

    @Test
    public void testBallKeepMovingWhenNoBricksOrWallsTouched() throws InterruptedException {
        Breakout breakout = new Breakout();
        BreakoutPanel panel = breakout.getPanel();
        Ball ball = panel.getBall();

        int initialY = ball.getY();

        panel.update();

        int expectedY = initialY + ball.getYVelocity();
        Assert.assertEquals(expectedY, ball.getY());
        Thread.sleep(3_000);

    }



}
