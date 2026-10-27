import React, { PropsWithChildren, useEffect, useRef, useState } from 'react';

type StationTimePeriod = 'day' | 'evening' | 'night';

export const stationTimePeriod = (hour: number): StationTimePeriod => {
  if (hour < 0 || hour > 23) {
    throw new RangeError('hour must be between 0 and 23');
  }
  if (hour >= 6 && hour <= 15) return 'day';
  if (hour >= 16 && hour <= 18) return 'evening';
  return 'night';
};

const PANEL_LEFT: Record<StationTimePeriod, number> = {
  day: 10,
  evening: 550,
  night: 1089,
};

const StationSceneBackground = ({ children }: PropsWithChildren) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [period, setPeriod] = useState(() => stationTimePeriod(new Date().getHours()));

  useEffect(() => {
    const timer = window.setInterval(() => {
      setPeriod(stationTimePeriod(new Date().getHours()));
    }, 60_000);
    return () => window.clearInterval(timer);
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    const container = canvas?.parentElement;
    if (!canvas || !container) return;

    const image = new Image();
    image.src = `${process.env.PUBLIC_URL}/station_time_scenes.png`;

    const draw = () => {
      if (!image.complete || image.naturalWidth === 0) return;
      const width = container.clientWidth;
      const height = container.clientHeight;
      if (width === 0 || height === 0) return;

      const pixelRatio = window.devicePixelRatio || 1;
      canvas.width = Math.round(width * pixelRatio);
      canvas.height = Math.round(height * pixelRatio);

      const panelWidth = 526 * image.naturalWidth / 1626;
      const panelHeight = 947 * image.naturalHeight / 967;
      const scale = Math.max(width / panelWidth, height / panelHeight);
      const cropWidth = width / scale;
      const cropHeight = height / scale;
      const sourceX = PANEL_LEFT[period] * image.naturalWidth / 1626
        + (panelWidth - cropWidth) / 2;
      const sourceY = 10 * image.naturalHeight / 967 + panelHeight - cropHeight;

      const context = canvas.getContext('2d');
      if (!context) return;
      context.clearRect(0, 0, canvas.width, canvas.height);
      context.drawImage(
        image,
        sourceX,
        sourceY,
        cropWidth,
        cropHeight,
        0,
        0,
        canvas.width,
        canvas.height,
      );
    };

    image.addEventListener('load', draw);
    const observer = new ResizeObserver(draw);
    observer.observe(container);
    draw();

    return () => {
      image.removeEventListener('load', draw);
      observer.disconnect();
    };
  }, [period]);

  return (
    <div className="station-scene">
      <canvas ref={canvasRef} className="station-scene__image" aria-hidden="true" />
      <div className="station-scene__veil" aria-hidden="true" />
      <div className="station-scene__content">{children}</div>
    </div>
  );
};

export default StationSceneBackground;
