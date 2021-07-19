// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using System.Collections.ObjectModel;

#if WINDOWS_UWP
using System.Linq;
using System.Threading.Tasks;
using Windows.ApplicationModel.Core;
using Windows.Foundation;
using Windows.UI.Core;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
#endif

namespace Adept
{
    /// <summary>
    /// Provides information about an application view.
    /// </summary>
    public class AppViewInfo
    {
        #region Member Variables
        private string name;
#if WINDOWS_UWP
        private CoreDispatcher dispatcher;
        private ApplicationView view;
#endif
        #endregion // Member Variables

        #region Constructors
        private AppViewInfo() { }

#if WINDOWS_UWP
        /// <summary>
        /// Initializes a new <see cref="AppViewInfo"/>.
        /// </summary>
        /// <param name="view">
        /// The <see cref="ApplicationView"/> this instance represents.
        /// </param>
        /// <param name="dispatcher">
        /// The <see cref="CoreDispatcher"/> where the view was created.
        /// </param>
        /// <param name="name">
        /// The name of the view.
        /// </param>
        public AppViewInfo(ApplicationView view, CoreDispatcher dispatcher, string name)
        {
            // Validate
            if (view == null) throw new ArgumentNullException(nameof(view));
            if (dispatcher == null) throw new ArgumentNullException(nameof(dispatcher));
            if (string.IsNullOrEmpty(name)) throw new ArgumentException(nameof(name));

            // Store
            this.view = view;
            this.dispatcher = dispatcher;
            this.name = name;

            // Subscribe to events for forwarding
            this.view.Consolidated += View_Consolidated;
        }
#endif
        #endregion // Constructors

        #region Public Methods
#if WINDOWS_UWP
        /// <summary>
        /// Gets the current content within the view.
        /// </summary>
        /// <returns>
        /// A <see cref="Task"/> that yields the current content in the view.
        /// </returns>
        /// <remarks>
        /// If the content in the view is a Frame, this method automatically returns 
        /// the content within the Frame. If you wish to access the Frame itself use 
        /// the <see cref="GetFrameAsync"/> method instead.
        /// </remarks>
        public async Task<UIElement> GetContentAsync()
        {
            // Placeholder
            UIElement content = null;

            // Run on dispatcher
            await dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                // Get the window content
                content = Window.Current?.Content;

                // If it's a frame, use the Frames content.
                var frame = content as Frame;
                if (frame!= null) { content = frame.Content as UIElement; }
            });

            // Done
            return content;
        }

        /// <summary>
        /// Gets the navigation frame for the view.
        /// </summary>
        /// <returns>
        /// A <see cref="Task"/> that yields the navigation frame for the view.
        /// </returns>
        public async Task<Frame> GetFrameAsync()
        {
            // Placeholder
            Frame frame = null;

            // Run on dispatcher
            await dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                // Get the frame
                frame = Window.Current?.Content as Frame;
            });

            // Done
            return frame;
        }
#endif

        /// <summary>
        /// Visually replaces the current view with this view.
        /// </summary>
        /// <remarks>
        /// When possible use <see cref="SwitchAsync"/> instead of this method. 
        /// <see cref="SwitchAsync"/> will not be visible to Unity behaviors 
        /// when in the editor. The async versions require UWP.
        /// </remarks>
        public void Switch()
        {
#if WINDOWS_UWP
            SwitchAsync().Wait();
#endif
        }

        public void Switch(AppViewInfo fromView)
        {
#if WINDOWS_UWP
            SwitchAsync(fromView, Windows.UI.ViewManagement.ApplicationViewSwitchingOptions.SkipAnimation).Wait();
#endif
        }

#if WINDOWS_UWP
        /// <summary>
        /// Visually replaces the current view with this view.
        /// </summary>
        /// <returns>
        /// A <see cref="Task"/> that represents the operation.
        /// </returns>
        public async Task SwitchAsync()
        {
            await dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
            {
                await ApplicationViewSwitcher.SwitchAsync(this.view.Id);
            });
        }

        /// <summary>
        /// Visually replaces the current view with this view.
        /// </summary>
        /// <param name="fromView">
        /// The view you are switching from.
        /// </param>
        /// <param name="options">
        /// Options for the display transition behaviors.
        /// </param>
        /// <returns>
        /// A <see cref="Task"/> that represents the operation.
        /// </returns>
        public async Task SwitchAsync(AppViewInfo fromView, ApplicationViewSwitchingOptions options)
        {
            // From view MUST be passed
            if (fromView == null) throw new ArgumentNullException(nameof(fromView));

            await dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
            {
                await ApplicationViewSwitcher.SwitchAsync(this.view.Id, fromView.view.Id, options);
            });
        }

        /// <summary>
        /// Attempts to display this view adjacent to the current view.
        /// </summary>
        /// <returns>
        /// <returns>
        /// A <see cref="Task"/> that <c>true</c> if the view window was shown; otherwise <c>false</c>.
        /// </returns>
        public async Task<bool> TryShowAsStandaloneAsync()
        {
            bool success = false;
            await dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
            {
                success = await ApplicationViewSwitcher.TryShowAsStandaloneAsync(this.view.Id);
            });
            return success;
        }
#endif
        #endregion // Public Methods

        #region Overrides / Event Handlers
#if WINDOWS_UWP
        private void View_Consolidated(ApplicationView sender, ApplicationViewConsolidatedEventArgs args)
        {
            if (Consolidated != null)
            {
                Consolidated(this, EventArgs.Empty);
            }
        }
#endif
        #endregion // Overrides / Event Handlers

        #region Public Properties
#if WINDOWS_UWP
        /// <summary>
        /// Gets the dispatcher that the view was created on.
        /// </summary>
        public CoreDispatcher Dispatcher => dispatcher;
#endif

        /// <summary>
        /// Gets the name of the view.
        /// </summary>
        public string Name { get { return name; } }

#if WINDOWS_UWP
        /// <summary>
        /// Gets the application view.
        /// </summary>
        public ApplicationView View => view;
#endif
        #endregion // Public Properties

        #region Public Events
        /// <summary>
        /// Occurs when the window is removed from the list of 
        /// recently used apps, or if the user executes a close gesture on it.
        /// </summary>
        public event EventHandler Consolidated;
        #endregion // Public Events
    }

    /// <summary>
    /// A specialized collection for app views.
    /// </summary>
    public class AppViewCollection : KeyedCollection<string, AppViewInfo>
    {
        protected override string GetKeyForItem(AppViewInfo item)
        {
            return item.Name;
        }
    }

    /// <summary>
    /// Manages Application AViews
    /// </summary>
    static public class AppViewManager
    {
        #region Member Variables
        static private AppViewCollection views = new AppViewCollection();
        #endregion // Member Variables

        #region Public Methods
#if WINDOWS_UWP
        /// <summary>
        /// Creates an <see cref="AppViewInfo"/> from the current dispatcher.
        /// </summary>
        /// <param name="name">
        /// The name of the view.
        /// </param>
        /// <returns>
        /// A <see cref="Task"/> that yields the <see cref="AppViewInfo"/> for the view in the current dispatcher.
        /// </returns>
        static public Task<AppViewInfo> CreateFromCurrentDispatcherAsync(string name)
        {
            return CreateFromDispatcherAsync(name, Window.Current.Dispatcher);
        }

        /// <summary>
        /// Creates an <see cref="AppViewInfo"/> from the specified dispatcher.
        /// </summary>
        /// <param name="name">
        /// The name of the view.
        /// </param>
        /// <param name="dispatcher">
        /// The dispatcher to get the view from.
        /// </param>
        /// <returns>
        /// A <see cref="Task"/> that yields the <see cref="AppViewInfo"/> for the current view in the dispatcher.
        /// </returns>
        static public async Task<AppViewInfo> CreateFromDispatcherAsync(string name, CoreDispatcher dispatcher)
        {
            // Validate 
            if (string.IsNullOrEmpty(name)) throw new ArgumentException(nameof(name));
            if (dispatcher == null) throw new ArgumentNullException(nameof(dispatcher));

            // Placeholders
            ApplicationView view = null;
            AppViewInfo info = null;

            // Use dispatcher to get ApplicationView
            await dispatcher.RunAsync(CoreDispatcherPriority.Normal, ()=>
            {
                // Get view
                view = ApplicationView.GetForCurrentView();
            
                // Create info class
                info = new AppViewInfo(view, dispatcher, name);
            });

            // Add to list
            views.Add(info);

            // Done
            return info;
        }

        /// <summary>
        /// Creates a new view and navigates to the specified page.
        /// </summary>
        /// <param name="name">
        /// The name of the view.
        /// </param>
        /// <param name="viewType">
        /// The type of page to navigate to.
        /// </param>
        /// <returns>
        /// A new <see cref="AppViewInfo"/> that representes the view.
        /// </returns>
        static public async Task<AppViewInfo> CreateNewAsync(string name, Type viewType)
        {
            // Validate
            if (string.IsNullOrEmpty(name)) throw new ArgumentException(nameof(name));
            if (viewType == null) throw new ArgumentNullException(nameof(viewType));

            // Create a new view
            var view = CoreApplication.CreateNewView();

            // Using the new dispatcher, create a frame and show the page
            await view.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                // Create the frame
                Frame frame = new Frame();

                // Navigate to the page
                frame.Navigate(viewType, null);

                // Show frame
                var window = Window.Current;
                window.Content = frame;
                window.Activate();
            });

            // Create the view for the dispatcher that is now showing the page
            return await CreateFromDispatcherAsync(name, view.Dispatcher);
        }
#endif
        #endregion // Public Methods

        #region Public Properties
        /// <summary>
        /// Gets the list of all created views.
        /// </summary>
        static public AppViewCollection Views { get { return views; } }
        #endregion // Public Properties
    }
}