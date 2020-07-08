package de.jlo.talend.tweak;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to arrange Swing windows
 * @author jan
 */
public class WindowHelper {

    private static Rectangle virtualScreenBounds = null;
    private static boolean enableWindowPositioning = true;
    private static int desktopPanelHeight = 30;
    private static ArrayList<Rectangle> listScreens = new ArrayList<Rectangle>();

    private WindowHelper() {}

    public static void enableWindowPositioning(boolean enable) {
        enableWindowPositioning = enable;
        if (enable) {
            System.setProperty("java.awt.Window.locationByPlatform", "false");
        } else {
            System.setProperty("java.awt.Window.locationByPlatform", "true");
        }
    }

    public static boolean isWindowPositioningEnabled() {
        return enableWindowPositioning;
    }

    public static void init() {
        virtualScreenBounds = retrieveVirtualGraphicsInfo();
    }

    private static void checkState() {
        if (virtualScreenBounds == null) {
            init();
        }
    }

    public static Rectangle retrieveVirtualGraphicsInfo() {
        Rectangle bounds = new Rectangle();
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsDevice gd = null;
        for (int j = 0; j < gs.length; j++) {
            gd = gs[j];
            Rectangle gdBounds = gd.getDefaultConfiguration().getBounds();
            bounds = bounds.union(gdBounds);
            listScreens.add(gdBounds);
        }
        return bounds;
    }

    public static int getAllScreenWidth() {
        checkState();
        return virtualScreenBounds.width;
    }

    public static int getAllScreenHeight() {
        checkState();
        return virtualScreenBounds.height;
    }

    public static Rectangle getCurrentScreenBounds(Window window) {
    	checkState();
    	for (Rectangle r : listScreens) {
    		if (r.contains(window.getLocation())) {
    			return r;
    		}
    	}
    	return null;
    }
    
    public static void locateAtRightSideOfScreen(Rectangle r, Window window) {
    	int x = r.x + r.width - window.getWidth() - 10;
    	int y = r.y + 20;
    	int height = r.height - 100;
    	int width = window.getWidth();
    	window.setBounds(x, y, width, height);
    }
    
    public static void locateAtLeftSideOfScreen(Rectangle r, Window window) {
    	int x = r.x;
    	int y = r.y;
    	int height = r.height;
    	int width = window.getWidth();
    	window.setBounds(x, y, width, height);
    }

    public static void checkAndCorrectWindowBounds(Window window) {
        checkState();
    	boolean mustCorrectWindow = false;
    	// check top x position
    	Point windowLocation = window.getLocation();
    	if (checkLocation(windowLocation)) {
    		mustCorrectWindow = true;
    	}
    	int newHeight = window.getHeight();
    	if (window.getHeight() + Math.abs(windowLocation.y) > virtualScreenBounds.height) {
    		newHeight = virtualScreenBounds.height - windowLocation.y - desktopPanelHeight;
    		mustCorrectWindow = true;
    	}
    	int newWidth = window.getWidth();
    	if (window.getWidth() + Math.abs(windowLocation.x) > virtualScreenBounds.width) {
    		newWidth = virtualScreenBounds.width - windowLocation.x;
    		mustCorrectWindow = true;
    	}
    	if (mustCorrectWindow) {
    		window.setBounds(windowLocation.x, windowLocation.y, newWidth, newHeight);
    	}
    }
    
    public static void arrangeWindowsHorizontal(List<? extends Window> listWindows) {
        final int count = listWindows.size();
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int windowHeight = screen.height / count;
        Window window;
        for (int i = 0; i < count; i++) {
            window = listWindows.get(i);
            window.setBounds(0, (windowHeight * i), screen.width, windowHeight);
            window.validate();
        }
    }

    public static void arrangeWindowsVertical(List<? extends Window> listWindows) {
        final int count = listWindows.size();
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int windowWidth = screen.width / count;
        Window window;
        for (int i = 0; i < count; i++) {
            window = listWindows.get(i);
            window.setBounds((windowWidth * i), 0, windowWidth, screen.height);
            checkAndCorrectWindowBounds(window);
            window.validate();
        }
    }

    public static void arrangeWindowsOverlapped(List<? extends Window> listWindows, Window topWindow, int defWidth, int defHeight) {
        Window window = null;
        int i = 0;
        for (; i < listWindows.size(); i++) {
            window = listWindows.get(i);
            if (window != topWindow) {
                window.setBounds(i * 50, i * 20, defWidth, defHeight);
                checkAndCorrectWindowBounds(window);
                window.validate();
                window.requestFocus();
            }
        }
        i++;
        topWindow.setBounds(i * 50, i * 20, defWidth, defHeight);
    }

    public static void locateWindowAtRightSide(Window parent, Window window) {
        checkState();
        if (enableWindowPositioning) {
            int x = parent.getBounds().x + parent.getBounds().width;
            int y = parent.getBounds().y;
            final int deltaX = (virtualScreenBounds.width - x + virtualScreenBounds.x) - window.getWidth();
            if (deltaX < 0) {
                x = x + deltaX;
            }
            final int deltaY = (virtualScreenBounds.height - y + virtualScreenBounds.y) - window.getHeight() + desktopPanelHeight;
            if (deltaY < 0) {
                y = y + deltaY;
            }
            int width = window.getSize().width;
            int height = parent.getHeight();
            window.setBounds(x, y, width, height);
            checkAndCorrectWindowBounds(window);
        }
    }

    public static void locateWindowAtRightSideWithin(Window parent, Window window, int xOffset) {
        checkState();
        if (enableWindowPositioning) {
            int x = parent.getBounds().x + parent.getBounds().width - window.getWidth() + xOffset;
            int y = parent.getBounds().y;
            final int deltaX = (virtualScreenBounds.width - x + virtualScreenBounds.x) - window.getWidth();
            if (deltaX < 0) {
                x = x + deltaX;
            }
            final int deltaY = (virtualScreenBounds.height - y + virtualScreenBounds.y) - window.getHeight() + desktopPanelHeight;
            if (deltaY < 0) {
                y = y + deltaY;
            }
            int width = window.getSize().width;
            int height = parent.getHeight();
            window.setBounds(x, y, width, height);
            checkAndCorrectWindowBounds(window);
        }
    }

    public static void locateWindowAtLeftSideWithin(Window parent, Window window, int xOffset) {
        checkState();
        if (enableWindowPositioning) {
            int x = parent.getBounds().x - (parent.getBounds().width + xOffset);
            int y = parent.getBounds().y;
            final int deltaX = (virtualScreenBounds.width - x + virtualScreenBounds.x) - window.getWidth();
            if (deltaX < 0) {
                x = x + deltaX;
            }
            final int deltaY = (virtualScreenBounds.height - y + virtualScreenBounds.y) - window.getHeight() + desktopPanelHeight;
            if (deltaY < 0) {
                y = y + deltaY;
            }
            int width = window.getSize().width;
            int height = parent.getHeight();
            window.setBounds(x, y, width, height);
            checkAndCorrectWindowBounds(window);
        }
    }
    
    public static void locateWindowAtMiddle(Window parent, Window window) {
        checkState();
        if (enableWindowPositioning) {
            int x = (parent.getX() + (parent.getWidth() >> 1)) - (window.getWidth() >> 1);
            int y = (parent.getY() + (parent.getHeight() >> 1)) - (window.getHeight() >> 1);
            final int deltaX = (virtualScreenBounds.width - x + virtualScreenBounds.x) - window.getWidth();
            if (deltaX < 0) {
                x = x + deltaX;
            }
            final int deltaY = (virtualScreenBounds.height - y + virtualScreenBounds.y) - window.getHeight() + desktopPanelHeight;
            if (deltaY < 0) {
                y = y + deltaY;
            }
            window.setLocation(x, y);
            checkAndCorrectWindowBounds(window);
        }
    }

    public static void locateWindowAtMiddleOfDefaultScreen(Window window) {
        checkState();
        if (enableWindowPositioning) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension defaultScreenDim = tk.getScreenSize();
            int x = (defaultScreenDim.width >> 1) - (window.getWidth() >> 1);
            int y = (defaultScreenDim.height >> 1) - (window.getHeight() >> 1);
            final int deltaX = (virtualScreenBounds.width - x + virtualScreenBounds.x) - window.getWidth();
            if (deltaX < 0) {
                x = x + deltaX;
            }
            final int deltaY = (virtualScreenBounds.height - y + virtualScreenBounds.y) - window.getHeight() + desktopPanelHeight;
            if (deltaY < 0) {
                y = y + deltaY;
            }
            window.setLocation(x, y);
            checkAndCorrectWindowBounds(window);
        }
    }
    
    /**
     * checks if a location is inside the visible screen
     * @param location
     * @return true if given object is changed because location is outside the visible screen
     */
    public static boolean checkLocation(Point location) {
        checkState();
    	boolean changed = true;
    	for (Rectangle r : listScreens) {
    		if (r.contains(location)) {
    			changed = false;
    			break;
    		}
    	}
    	if (changed) {
            location.x = 0;
            location.y = 10;
    	}
    	return changed;
    }
    
    public static int getScreenCount() {
    	return listScreens.size();
    }
    
    public static Rectangle[] getScreensBounds() {
    	Rectangle[] rects = new Rectangle[listScreens.size()];
    	int i = 0;
    	for (Rectangle r : listScreens) {
    		rects[i] = r;
    		i++;
    	}
    	return rects;
    }

}
