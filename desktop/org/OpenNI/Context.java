/****************************************************************************
*                                                                           *
*  OpenNI 1.x Alpha                                                         *
*  Copyright (C) 2011 PrimeSense Ltd.                                       *
*                                                                           *
*  This file is part of OpenNI.                                             *
*                                                                           *
*  OpenNI is free software: you can redistribute it and/or modify           *
*  it under the terms of the GNU Lesser General Public License as published *
*  by the Free Software Foundation, either version 3 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  OpenNI is distributed in the hope that it will be useful,                *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of           *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the             *
*  GNU Lesser General Public License for more details.                      *
*                                                                           *
*  You should have received a copy of the GNU Lesser General Public License *
*  along with OpenNI. If not, see <http://www.gnu.org/licenses/>.           *
*                                                                           *
****************************************************************************/
package org.OpenNI;

import java.util.Hashtable;

public class Context extends ObjectWrapper
{
	public Context() throws GeneralException
	{
		this(init(), false);
	}
	
	public static Context createFromXmlFile(String xmlFile, OutArg<ScriptNode> scriptNode) throws GeneralException
	{
		OutArg<Long> hScript = new OutArg<Long>();
		long pContext = Context.initFromXmlEx(xmlFile, hScript);
		Context context = new Context(pContext, false);
		scriptNode.value = new ScriptNode(context, hScript.value, false);
		return context;
	}
	
	public static Context fromNative(long pContext) throws GeneralException
	{
		synchronized (Context.allContexts)
		{
			if (Context.allContexts.containsKey(pContext))
			{
				return Context.allContexts.get(pContext);
			}
			else
			{
				return new Context(pContext);
			}
		}
	}
	
	public static Version getVersion() throws StatusException
	{
		OutArg<Version> pVersion = new OutArg<Version>();
		int status = NativeMethods.xnGetVersion(pVersion);
		WrapperUtils.throwOnError(status);
		return pVersion.value;
	}
	
	public ScriptNode runXmlScriptFromFile(String scriptFile) throws GeneralException
	{
		EnumerationErrors errors = new EnumerationErrors();
		OutArg<Long> phScriptNode = new OutArg<Long>();
		int status = NativeMethods.xnContextRunXmlScriptFromFileEx(toNative(), scriptFile, errors.toNative(), phScriptNode);
		WrapperUtils.checkEnumeration(status, errors);
		return new ScriptNode(this, phScriptNode.value, false);
	}

	public ScriptNode runXmlScript(String script) throws GeneralException
	{
		EnumerationErrors errors = new EnumerationErrors();
		OutArg<Long> phScriptNode = new OutArg<Long>();
		int status = NativeMethods.xnContextRunXmlScriptEx(toNative(), script, errors.toNative(), phScriptNode);
		WrapperUtils.checkEnumeration(status, errors);
		return new ScriptNode(this, phScriptNode.value, false);
	}

	public Player openFileRecordingEx(String fileName) throws GeneralException
	{
		OutArg<Long> phPlayer = new OutArg<Long>();
		int status = NativeMethods.xnContextOpenFileRecordingEx(toNative(), fileName, phPlayer);
		WrapperUtils.throwOnError(status);
		return (Player)createProductionNodeFromNative(phPlayer.value);
	}
	
	public void release()
	{
		dispose();
	}
	
	public void addLicense(License license) throws StatusException
	{
		int status = NativeMethods.xnAddLicense(toNative(), license.getVendor(), license.getKey());
		WrapperUtils.throwOnError(status);
	}
	
	public License[] enumerateLicenses() throws StatusException
	{
		OutArg<License[]> licenses = new OutArg<License[]>();
		int status = NativeMethods.xnEnumerateLicenses(toNative(), licenses);
		WrapperUtils.throwOnError(status);
		return licenses.value;
	}
	
	public NodeInfoList enumerateProductionTrees(NodeType type, Query query) throws GeneralException
	{
		EnumerationErrors errors = new EnumerationErrors();
		OutArg<Long> ppNodesList = new OutArg<Long>();
		int status = NativeMethods.xnEnumerateProductionTrees(toNative(), type.toNative(),
				query == null ? 0 : query.toNative(),
				ppNodesList,
				errors.toNative());
		WrapperUtils.checkEnumeration(status, errors);
		return new NodeInfoList(ppNodesList.value);
	}

	public NodeInfoList enumerateProductionTrees(NodeType type) throws GeneralException
	{
		return enumerateProductionTrees(type, null);
	}
	
	public ProductionNode createProductionTree(NodeInfo nodeInfo) throws GeneralException
	{
		OutArg<Long> phNode = new OutArg<Long>();
		int status = NativeMethods.xnCreateProductionTree(toNative(), nodeInfo.toNative(), phNode);
		WrapperUtils.throwOnError(status);
		return createProductionNodeObject(phNode.value);
	}
	
	public ProductionNode createAnyProductionTree(NodeType type, Query query) throws GeneralException
	{
		EnumerationErrors errors = new EnumerationErrors();
		OutArg<Long> phNode = new OutArg<Long>();
		int status = NativeMethods.xnCreateAnyProductionTree(toNative(), type.toNative(),
				query == null ? 0 : query.toNative(),
				phNode,
				errors.toNative());
		WrapperUtils.checkEnumeration(status, errors);
		return createProductionNodeFromNative(phNode.value);
	}

	public NodeInfoList enumerateExistingNodes() throws GeneralException
	{
		OutArg<Long> ppList = new OutArg<Long>();
		int status = NativeMethods.xnEnumerateExistingNodes(this.toNative(), ppList);
		WrapperUtils.throwOnError(status);
		return new NodeInfoList(ppList.value);
	}

	public NodeInfoList enumerateExistingNodes(NodeType type) throws GeneralException
	{
		OutArg<Long> ppList = new OutArg<Long>();
		int status = NativeMethods.xnEnumerateExistingNodesByType(this.toNative(), type.toNative(), ppList);
		WrapperUtils.throwOnError(status);
		return new NodeInfoList(ppList.value);
	}

	public ProductionNode findExistingNode(NodeType type) throws GeneralException
	{
		OutArg<Long> phNode = new OutArg<Long>();
		int status = NativeMethods.xnFindExistingRefNodeByType(this.toNative(), type.toNative(), phNode);
		WrapperUtils.throwOnError(status);
		ProductionNode node = createProductionNodeObject(phNode.value, type);
		// release the handle
		NativeMethods.xnProductionNodeRelease(phNode.value);

		return node;
	}

	public ProductionNode getProductionNodeByName(String name) throws GeneralException
	{
		OutArg<Long> phNode = new OutArg<Long>();
		int status = NativeMethods.xnGetRefNodeHandleByName(this.toNative(), name, phNode);
		WrapperUtils.throwOnError(status);
		ProductionNode node = createProductionNodeObject(phNode.value);
		// release the handle
		NativeMethods.xnProductionNodeRelease(phNode.value);

		return node;
	}

	public NodeInfo getProductionNodeInfoByName(String name) throws GeneralException
	{
		return getProductionNodeByName(name).getInfo();
	}

	public void startGeneratingAll() throws StatusException
	{
		int status = NativeMethods.xnStartGeneratingAll(this.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public void stopGeneratingAll() throws StatusException
	{
		int status = NativeMethods.xnStopGeneratingAll(this.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public boolean getGlobalMirror()
	{
		return NativeMethods.xnGetGlobalMirror(toNative());
	}
	
	public void setGlobalMirror(boolean mirror) throws StatusException
	{
		int status = NativeMethods.xnSetGlobalMirror(toNative(), mirror);
		WrapperUtils.throwOnError(status);
	}
	
	public void waitAndUpdateAll() throws StatusException
	{
		int status = NativeMethods.xnWaitAndUpdateAll(this.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public void waitOneUpdateAll(ProductionNode waitFor) throws StatusException
	{
		int status = NativeMethods.xnWaitOneUpdateAll(toNative(), waitFor.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public void waitAnyUpdateAll() throws StatusException
	{
		int status = NativeMethods.xnWaitAnyUpdateAll(this.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public void waitNoneUpdateAll() throws StatusException
	{
		int status = NativeMethods.xnWaitNoneUpdateAll(this.toNative());
		WrapperUtils.throwOnError(status);
	}
	
	public IObservable<ErrorStateEventArgs> getErrorStateChangedEvent()
	{
		return errorStateChangedEvent;
	}
	
	public static ProductionNode createProductionNodeFromNative(long nodeHandle) throws GeneralException
	{
		long pContext = NativeMethods.xnGetRefContextFromNodeHandle(nodeHandle);
		Context context = Context.fromNative(pContext);
		NativeMethods.xnContextRelease(pContext);
		return context.createProductionNodeObject(nodeHandle);
	}

	protected void freeObject(long ptr)
	{
		synchronized (Context.allContexts) 
		{
			Context.allContexts.remove(ptr);
		}
		NativeMethods.xnContextRelease(ptr);
	}
	
	private Context(long pContext, boolean addRef) throws GeneralException
	{
		super(pContext);
		
		errorStateChangedEvent = new Observable<ErrorStateEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToGlobalErrorStateChange(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromGlobalErrorStateChange(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int status)
			{
				notify(new ErrorStateEventArgs(status));
			}
		};
		
		synchronized (Context.allContexts) 
		{
			if (Context.allContexts.containsKey(pContext))
			{
				throw new GeneralException("Java wrapper: creating a Context object wrapping an already wrapped object!");
			}
			Context.allContexts.put(pContext, this);
		}
		
		if (addRef)
		{
			WrapperUtils.throwOnError(NativeMethods.xnContextAddRef(pContext));
		}
	}
	
	private Context(long pContext) throws GeneralException
	{
		this(pContext, true);
	}

	private static long init() throws StatusException
	{
		OutArg<Long> pContext = new OutArg<Long>();
		int status = NativeMethods.xnInit(pContext);
		WrapperUtils.throwOnError(status);
		return pContext.value;
	}
	
	private static long initFromXmlEx(String xmlFile, OutArg<Long> hScriptNode) throws StatusException
	{
		OutArg<Long> pContext = new OutArg<Long>();
		int status = NativeMethods.xnInitFromXmlFileEx(xmlFile, pContext, 0L, hScriptNode);
		WrapperUtils.throwOnError(status);
		return pContext.value;
	}
	
	ProductionNode createProductionNodeObject(long nodeHandle, NodeType type) throws GeneralException
	{
		synchronized (this.allNodes)
		{
			if (!this.allNodes.containsKey(nodeHandle))
			{
				// create it
				ProductionNode node;
				
				if (type.equals(NodeType.DEVICE))
					node = new Device(this, nodeHandle, true);
				else if (type.equals(NodeType.DEPTH))
					node = new DepthGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.IMAGE))
					node = new ImageGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.AUDIO))
					node = new AudioGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.IR))
					node = new IRGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.USER))
					node = new UserGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.RECORDER))
					node = new Recorder(this, nodeHandle, true);
				else if (type.equals(NodeType.PLAYER))
					node = new Player(this, nodeHandle, true);
				else if (type.equals(NodeType.GESTURE))
					node = new GestureGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.SCENE))
					node = new SceneAnalyzer(this, nodeHandle, true);
				else if (type.equals(NodeType.HANDS))
					node = new HandsGenerator(this, nodeHandle, true);
				else if (type.equals(NodeType.CODEC))
					node = new Codec(this, nodeHandle, true);
				else if (type.equals(NodeType.SCRIPT_NODE))
					node = new ScriptNode(this, nodeHandle, true);
				else if (type.equals(NodeType.PRODUCTION_NODE))
					node = new ProductionNode(this, nodeHandle, true);
				else if (type.equals(NodeType.GENERATOR))
					node = new Generator(this, nodeHandle, true);
				else if (type.equals(NodeType.MAP_GENERATOR))
					node = new MapGenerator(this, nodeHandle, true);
				else
					throw new GeneralException("java wrapper: Unknown generator type!");
				
				// add it to map
				this.allNodes.put(nodeHandle, node);
			} // create
			
			return this.allNodes.get(nodeHandle);
		} // synch
	}

	ProductionNode createProductionNodeObject(long hNode) throws GeneralException
	{
		long pNodeInfo = NativeMethods.xnGetNodeInfo(hNode);
		NodeType type = NativeMethods.xnNodeInfoGetDescription(pNodeInfo).getType();
		return createProductionNodeObject(hNode, type);
	}

	private Observable<ErrorStateEventArgs> errorStateChangedEvent;
	private Hashtable<Long, ProductionNode> allNodes = new Hashtable<Long, ProductionNode>();
	
	private static Hashtable<Long, Context> allContexts = new Hashtable<Long, Context>(); 
}
