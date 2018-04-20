using System;
using System.IO;
using UnityEngine;

public static class CoherentUIGTFileUtilities
{
	public static void DirectoryCopy(string sourceDirName,
	                                 string destDirName,
	                                 bool copySubDirs,
	                                 string[] includedFiles,
	                                 string[] excludedFiles,
	                                 bool deleteSource)
	{
		DirectoryInfo dir = new DirectoryInfo(sourceDirName);
		DirectoryInfo[] dirs = dir.GetDirectories();

		if (!dir.Exists)
		{
			throw new DirectoryNotFoundException("Source directory does not " +
			"exist or could not be found: " +
			sourceDirName);
		}

		if (destDirName != null && !Directory.Exists(destDirName))
		{
			Directory.CreateDirectory(destDirName);
		}

		FileInfo[] files = dir.GetFiles();

		foreach (FileInfo file in files)
		{
			if (excludedFiles != null && excludedFiles.Length > 0)
			{
				bool isExcluded = Array.Exists(excludedFiles, (x) =>
				{
					if (x.StartsWith("*."))
					{
						return x.Substring(1) == file.Extension;
					}
					else
					{
						return x == file.Name;
					}
				});

				if (isExcluded)
				{
					continue;
				}
			}

			if (includedFiles != null && includedFiles.Length > 0)
			{
				bool isIncluded = Array.Exists(includedFiles, (x) =>
				{
					if (x.StartsWith("*."))
					{
						return x.Substring(1) == file.Extension;
					}
					else
					{
						return x == file.Name;
					}
				});

				if (!isIncluded)
				{
					continue;
				}
			}

			if (destDirName != null)
			{
				string temppath = Path.Combine(destDirName, file.Name);

				try
				{
					file.CopyTo(temppath, true);
				} 
				catch (UnauthorizedAccessException ex)
				{
					Debug.LogError("Failed to copy " + file.Name + "\n" + ex.Message);
					continue;
				}
			}

			if (deleteSource)
			{
				try
				{
					file.Delete();
				} 
				catch (IOException ex)
				{
					Debug.LogError("Failed to remove source file " + file.Name + "\n" + ex.Message);
					continue;
				}

			}
		}

		if (copySubDirs)
		{
			foreach (DirectoryInfo subdir in dirs)
			{
				if (destDirName != null)
				{
					string temppath = Path.Combine(destDirName, subdir.Name);
					DirectoryCopy(subdir.FullName, temppath, copySubDirs, includedFiles, excludedFiles, deleteSource);
				}
				else
				{
					DirectoryCopy(subdir.FullName, null, copySubDirs, includedFiles, excludedFiles, deleteSource);
				}
			}
		}
	}
}
