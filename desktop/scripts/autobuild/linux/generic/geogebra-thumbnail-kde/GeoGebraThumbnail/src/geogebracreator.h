/*
 *  Copyright (C) 2009
 *  by Ariel Constenla-Haile <ariel.constenla.haile@gmail.com>
 *
 * This file is part of GeoGebra Thumbnail Creator.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * This is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with GeoGebra Thumbnail Creator.  If not, see
 * <http://www.gnu.org/licenses/> for a copy of the LGPLv3 License.
 */

#ifndef _GEOGEBRA_CREATOR_H_
#define _GEOGEBRA_CREATOR_H_

#include <QObject>
#include <kio/thumbcreator.h>

class GeoGebraCreator : public QObject, public ThumbCreator
{
    Q_OBJECT
public:
    GeoGebraCreator();
    virtual ~GeoGebraCreator();
    virtual bool create(const QString &path, int w, int h, QImage &img);
    virtual Flags flags() const;
};

#endif // _GEOGEBRA_CREATOR_H_
