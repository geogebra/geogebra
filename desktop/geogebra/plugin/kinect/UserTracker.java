package geogebra.plugin.kinect;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ShortBuffer;
import java.util.HashMap;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.OutArg;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.PoseDetectionEventArgs;
import org.OpenNI.SceneMetaData;
import org.OpenNI.ScriptNode;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;

public class UserTracker extends Component
{
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("New user " + args.getId());
			try
			{
				poseDetectionCap.StartPoseDetection(calibPose, args.getId());
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class LostUserObserver implements IObserver<UserEventArgs>
	{
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("Lost use " + args.getId());
			joints.remove(args.getId());
		}
	}
	
	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
	{
		public void update(IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args)
		{
			System.out.println("Calibraion complete: " + args.getStatus());
			try
			{
			if (args.getStatus() == CalibrationProgressStatus.OK)
			{
				System.out.println("starting tracking "  +args.getUser());
					skeletonCap.startTracking(args.getUser());
	                joints.put(new Integer(args.getUser()), new HashMap<SkeletonJoint, SkeletonJointPosition>());
			}
			else
			{
                poseDetectionCap.StartPoseDetection(calibPose, args.getUser());
			}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
	{
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args)
		{
			System.out.println("Pose " + args.getPose() + " detected for " + args.getUser());
			try
			{
				poseDetectionCap.StopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutArg<ScriptNode> scriptNode;
    private Context context;
    private DepthGenerator depthGen;
    private UserGenerator userGen;
    private SkeletonCapability skeletonCap;
    private PoseDetectionCapability poseDetectionCap;
    private byte[] imgbytes;
    private float histogram[];
    String calibPose = null;
    HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;

    private boolean drawBackground = true;
    private boolean drawPixels = true;
    private boolean drawSkeleton = true;
    private boolean printID = true;
    private boolean printState = true;
    
    
    private BufferedImage bimg;
    int width, height;
    
	private final String SAMPLE_XML_FILE = "geogebra/plugin/kinect/SamplesConfig.xml";
    public UserTracker()
    {

        try {
            scriptNode = new OutArg<ScriptNode>();
            context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);

            depthGen = DepthGenerator.create(context);
            DepthMetaData depthMD = depthGen.getMetaData();

            histogram = new float[10000];
            width = depthMD.getFullXRes();
            height = depthMD.getFullYRes();
            
            imgbytes = new byte[width*height*3];

            userGen = UserGenerator.create(context);
            skeletonCap = userGen.getSkeletonCapability();
            poseDetectionCap = userGen.getPoseDetectionCapability();
            
            userGen.getNewUserEvent().addObserver(new NewUserObserver());
            userGen.getLostUserEvent().addObserver(new LostUserObserver());
            skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
            poseDetectionCap.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
            
            calibPose = skeletonCap.getSkeletonCalibrationPose();
            joints = new HashMap<Integer, HashMap<SkeletonJoint,SkeletonJointPosition>>();
            
            skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
			
			context.startGeneratingAll();
        } catch (GeneralException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void calcHist(ShortBuffer depth)
    {
        // reset
        for (int i = 0; i < histogram.length; ++i)
            histogram[i] = 0;
        
        depth.rewind();

        int points = 0;
        while(depth.remaining() > 0)
        {
            short depthVal = depth.get();
            if (depthVal != 0)
            {
                histogram[depthVal]++;
                points++;
            }
        }
        
        for (int i = 1; i < histogram.length; i++)
        {
            histogram[i] += histogram[i-1];
        }

        if (points > 0)
        {
            for (int i = 1; i < histogram.length; i++)
            {
                histogram[i] = 1.0f - (histogram[i] / (float)points);
            }
        }
    }


    void updateDepth()
    {
        try {

            context.waitAnyUpdateAll();

            DepthMetaData depthMD = depthGen.getMetaData();
            SceneMetaData sceneMD = userGen.getUserPixels(0);

            ShortBuffer scene = sceneMD.getData().createShortBuffer();
            ShortBuffer depth = depthMD.getData().createShortBuffer();
            calcHist(depth);
            depth.rewind();
            
            while(depth.remaining() > 0)
            {
                int pos = depth.position();
                short pixel = depth.get();
                short user = scene.get();
                
        		imgbytes[3*pos] = 0;
        		imgbytes[3*pos+1] = 0;
        		imgbytes[3*pos+2] = 0;                	

                if (drawBackground || pixel != 0)
                {
                	int colorID = user % (colors.length-1);
                	if (user == 0)
                	{
                		colorID = colors.length-1;
                	}
                	if (pixel != 0)
                	{
                		float histValue = histogram[pixel];
                		imgbytes[3*pos] = (byte)(histValue*colors[colorID].getRed());
                		imgbytes[3*pos+1] = (byte)(histValue*colors[colorID].getGreen());
                		imgbytes[3*pos+2] = (byte)(histValue*colors[colorID].getBlue());
                	}
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    Color colors[] = {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.WHITE};
    public void getJoint(int user, SkeletonJoint joint) throws StatusException
    {
        SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user, joint);
		if (pos.getPosition().getZ() != 0)
		{
			joints.get(user).put(joint, new SkeletonJointPosition(depthGen.convertRealWorldToProjective(pos.getPosition()), pos.getConfidence()));
		}
		else
		{
			joints.get(user).put(joint, new SkeletonJointPosition(new Point3D(), 0));
		}
    }
    public void getJoints(int user) throws StatusException
    {
    	getJoint(user, SkeletonJoint.HEAD);
    	getJoint(user, SkeletonJoint.NECK);
    	
    	getJoint(user, SkeletonJoint.LEFT_SHOULDER);
    	getJoint(user, SkeletonJoint.LEFT_ELBOW);
    	getJoint(user, SkeletonJoint.LEFT_HAND);

    	getJoint(user, SkeletonJoint.RIGHT_SHOULDER);
    	getJoint(user, SkeletonJoint.RIGHT_ELBOW);
    	getJoint(user, SkeletonJoint.RIGHT_HAND);

    	getJoint(user, SkeletonJoint.TORSO);

    	getJoint(user, SkeletonJoint.LEFT_HIP);
        getJoint(user, SkeletonJoint.LEFT_KNEE);
        getJoint(user, SkeletonJoint.LEFT_FOOT);

    	getJoint(user, SkeletonJoint.RIGHT_HIP);
        getJoint(user, SkeletonJoint.RIGHT_KNEE);
        getJoint(user, SkeletonJoint.RIGHT_FOOT);

    }
    void drawLine(Graphics g, HashMap<SkeletonJoint, SkeletonJointPosition> jointHash, SkeletonJoint joint1, SkeletonJoint joint2)
    {
		Point3D pos1 = jointHash.get(joint1).getPosition();
		Point3D pos2 = jointHash.get(joint2).getPosition();

		if (jointHash.get(joint1).getConfidence() == 0 || jointHash.get(joint1).getConfidence() == 0)
			return;

		g.drawLine((int)pos1.getX(), (int)pos1.getY(), (int)pos2.getX(), (int)pos2.getY());
    }
    public void drawSkeleton(Graphics g, int user) throws StatusException
    {
    	getJoints(user);
    	HashMap<SkeletonJoint, SkeletonJointPosition> dict = joints.get(new Integer(user));

    	drawLine(g, dict, SkeletonJoint.HEAD, SkeletonJoint.NECK);

    	drawLine(g, dict, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.TORSO);
    	drawLine(g, dict, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.TORSO);

    	drawLine(g, dict, SkeletonJoint.NECK, SkeletonJoint.LEFT_SHOULDER);
    	drawLine(g, dict, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.LEFT_ELBOW);
    	drawLine(g, dict, SkeletonJoint.LEFT_ELBOW, SkeletonJoint.LEFT_HAND);

    	drawLine(g, dict, SkeletonJoint.NECK, SkeletonJoint.RIGHT_SHOULDER);
    	drawLine(g, dict, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.RIGHT_ELBOW);
    	drawLine(g, dict, SkeletonJoint.RIGHT_ELBOW, SkeletonJoint.RIGHT_HAND);

    	drawLine(g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.TORSO);
    	drawLine(g, dict, SkeletonJoint.RIGHT_HIP, SkeletonJoint.TORSO);
    	drawLine(g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.RIGHT_HIP);

    	drawLine(g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.LEFT_KNEE);
    	drawLine(g, dict, SkeletonJoint.LEFT_KNEE, SkeletonJoint.LEFT_FOOT);

    	drawLine(g, dict, SkeletonJoint.RIGHT_HIP, SkeletonJoint.RIGHT_KNEE);
    	drawLine(g, dict, SkeletonJoint.RIGHT_KNEE, SkeletonJoint.RIGHT_FOOT);

    }
    
    public void paint(Graphics g)
    {
    	if (drawPixels)
    	{
            DataBufferByte dataBuffer = new DataBufferByte(imgbytes, width*height*3);

            WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null); 

            ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

            bimg = new BufferedImage(colorModel, raster, false, null);

    		g.drawImage(bimg, 0, 0, null);
    	}
        try
		{
			int[] users = userGen.getUsers();
			for (int i = 0; i < users.length; ++i)
			{
		    	Color c = colors[users[i]%colors.length];
		    	c = new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());

		    	g.setColor(c);
				if (drawSkeleton && skeletonCap.isSkeletonTracking(users[i]))
				{
					drawSkeleton(g, users[i]);
				}
				
				if (printID)
				{
					Point3D com = depthGen.convertRealWorldToProjective(userGen.getUserCoM(users[i]));
					String label = null;
					if (!printState)
					{
						label = new String(""+users[i]);
					}
					else if (skeletonCap.isSkeletonTracking(users[i]))
					{
						// Tracking
						label = new String(users[i] + " - Tracking");
					}
					else if (skeletonCap.isSkeletonCalibrating(users[i]))
					{
						// Calibrating
						label = new String(users[i] + " - Calibrating");
					}
					else
					{
						// Nothing
						label = new String(users[i] + " - Looking for pose (" + calibPose + ")");
					}

					g.drawString(label, (int)com.getX(), (int)com.getY());
				}
			}
		} catch (StatusException e)
		{
			e.printStackTrace();
		}
    }
}

