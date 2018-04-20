

var cameraTransform : Transform;
private var _target : Transform;

// The distance in the x-z plane to the target

var distance = 7.0;

// the height we want the camera to be above the target
var height = 3.0;

var angularSmoothLag = 0.3;
var angularMaxSpeed = 15.0;

var heightSmoothLag = 0.3;

var snapSmoothLag = 0.2;
var snapMaxSpeed = 720.0;

var clampHeadPositionScreenSpace = 0.75;

var lockCameraTimeout = 0.2;

private var headOffset = Vector3.zero;
private var centerOffset = Vector3.zero;

private var heightVelocity = 0.0;
private var angleVelocity = 0.0;
private var snap = false;
private var controller : ThirdPersonController;
private var targetHeight = 100000.0; 

function Awake ()
{
	if(!cameraTransform && Camera.main)
		cameraTransform = Camera.main.transform;
	if(!cameraTransform) {
		Debug.Log("Please assign a camera to the ThirdPersonCamera script.");
		enabled = false;	
	}
			
		
	_target = transform;
	if (_target)
	{
		controller = _target.GetComponent(ThirdPersonController);
	}
	
	if (controller)
	{
		var characterController : CharacterController = _target.GetComponent.<Collider>();
		centerOffset = characterController.bounds.center - _target.position;
		headOffset = centerOffset;
		headOffset.y = characterController.bounds.max.y - _target.position.y;
	}
	else
		Debug.Log("Please assign a target to the camera that has a ThirdPersonController script attached.");

	
	Cut(_target, centerOffset);
}

function DebugDrawStuff ()
{
	Debug.DrawLine(_target.position, _target.position + headOffset);

}

function AngleDistance (a : float, b : float)
{
	a = Mathf.Repeat(a, 360);
	b = Mathf.Repeat(b, 360);
	
	return Mathf.Abs(b - a);
}

function Apply (dummyTarget : Transform, dummyCenter : Vector3)
{
	// Early out if we don't have a target
	if (!controller)
		return;
	
	var targetCenter = _target.position + centerOffset;
	var targetHead = _target.position + headOffset;

//	DebugDrawStuff();

	// Calculate the current & target rotation angles
	var originalTargetAngle = _target.eulerAngles.y;
	var currentAngle = cameraTransform.eulerAngles.y;

	// Adjust real target angle when camera is locked
	var targetAngle = originalTargetAngle; 
	
	// When pressing Fire2 (alt) the camera will snap to the target direction real quick.
	// It will stop snapping when it reaches the target
	if (Input.GetButton("Fire2"))
		snap = true;
	
	if (snap)
	{
		// We are close to the target, so we can stop snapping now!
		if (AngleDistance (currentAngle, originalTargetAngle) < 3.0)
			snap = false;
		
		currentAngle = Mathf.SmoothDampAngle(currentAngle, targetAngle, angleVelocity, snapSmoothLag, snapMaxSpeed);
	}
	// Normal camera motion
	else
	{
		if (controller.GetLockCameraTimer () < lockCameraTimeout)
		{
			targetAngle = currentAngle;
		}

		// Lock the camera when moving backwards!
		// * It is really confusing to do 180 degree spins when turning around.
		if (AngleDistance (currentAngle, targetAngle) > 160 && controller.IsMovingBackwards ())
			targetAngle += 180;

		currentAngle = Mathf.SmoothDampAngle(currentAngle, targetAngle, angleVelocity, angularSmoothLag, angularMaxSpeed);
	}


	// When jumping don't move camera upwards but only down!
	if (controller.IsJumping ())
	{
		// We'd be moving the camera upwards, do that only if it's really high
		var newTargetHeight = targetCenter.y + height;
		if (newTargetHeight < targetHeight || newTargetHeight - targetHeight > 5)
			targetHeight = targetCenter.y + height;
	}
	// When walking always update the target height
	else
	{
		targetHeight = targetCenter.y + height;
	}

	// Damp the height
	var currentHeight = cameraTransform.position.y;
	currentHeight = Mathf.SmoothDamp (currentHeight, targetHeight, heightVelocity, heightSmoothLag);

	// Convert the angle into a rotation, by which we then reposition the camera
	var currentRotation = Quaternion.Euler (0, currentAngle, 0);
	
	// Set the position of the camera on the x-z plane to:
	// distance meters behind the target
	cameraTransform.position = targetCenter;
	cameraTransform.position += currentRotation * Vector3.back * distance;

	// Set the height of the camera
	cameraTransform.position.y = currentHeight;
	
	// Always look at the target	
	SetUpRotation(targetCenter, targetHead);
}

function LateUpdate () {
	Apply (transform, Vector3.zero);
}

function Cut (dummyTarget : Transform, dummyCenter : Vector3)
{
	var oldHeightSmooth = heightSmoothLag;
	var oldSnapMaxSpeed = snapMaxSpeed;
	var oldSnapSmooth = snapSmoothLag;
	
	snapMaxSpeed = 10000;
	snapSmoothLag = 0.001;
	heightSmoothLag = 0.001;
	
	snap = true;
	Apply (transform, Vector3.zero);
	
	heightSmoothLag = oldHeightSmooth;
	snapMaxSpeed = oldSnapMaxSpeed;
	snapSmoothLag = oldSnapSmooth;
}

function SetUpRotation (centerPos : Vector3, headPos : Vector3)
{
	// Now it's getting hairy. The devil is in the details here, the big issue is jumping of course.
	// * When jumping up and down we don't want to center the guy in screen space.
	//  This is important to give a feel for how high you jump and avoiding large camera movements.
	//   
	// * At the same time we dont want him to ever go out of screen and we want all rotations to be totally smooth.
	//
	// So here is what we will do:
	//
	// 1. We first find the rotation around the y axis. Thus he is always centered on the y-axis
	// 2. When grounded we make him be centered
	// 3. When jumping we keep the camera rotation but rotate the camera to get him back into view if his head is above some threshold
	// 4. When landing we smoothly interpolate towards centering him on screen
	var cameraPos = cameraTransform.position;
	var offsetToCenter = centerPos - cameraPos;
	
	// Generate base rotation only around y-axis
	var yRotation = Quaternion.LookRotation(Vector3(offsetToCenter.x, 0, offsetToCenter.z));

	var relativeOffset = Vector3.forward * distance + Vector3.down * height;
	cameraTransform.rotation = yRotation * Quaternion.LookRotation(relativeOffset);

	// Calculate the projected center position and top position in world space
	var centerRay = cameraTransform.GetComponent.<Camera>().ViewportPointToRay(Vector3(.5, 0.5, 1));
	var topRay = cameraTransform.GetComponent.<Camera>().ViewportPointToRay(Vector3(.5, clampHeadPositionScreenSpace, 1));

	var centerRayPos = centerRay.GetPoint(distance);
	var topRayPos = topRay.GetPoint(distance);
	
	var centerToTopAngle = Vector3.Angle(centerRay.direction, topRay.direction);
	
	var heightToAngle = centerToTopAngle / (centerRayPos.y - topRayPos.y);

	var extraLookAngle = heightToAngle * (centerRayPos.y - centerPos.y);
	if (extraLookAngle < centerToTopAngle)
	{
		extraLookAngle = 0;
	}
	else
	{
		extraLookAngle = extraLookAngle - centerToTopAngle;
		cameraTransform.rotation *= Quaternion.Euler(-extraLookAngle, 0, 0);
	}
}

function GetCenterOffset ()
{
	return centerOffset;
}
