import { ref, onMounted } from 'vue';

/**
 * 动画效果 Hook
 * 提供页面动画和过渡效果
 */
export function useAnimation() {
  const isVisible = ref(false);
  const cardDelay = ref(0);

  const fadeInUp = (delay: number = 0) => ({
    animation: `fadeInUp 0.5s ease-out ${delay}s forwards`,
    opacity: 0,
  });

  const fadeInLeft = (delay: number = 0) => ({
    animation: `fadeInLeft 0.5s ease-out ${delay}s forwards`,
    opacity: 0,
  });

  const fadeInRight = (delay: number = 0) => ({
    animation: `fadeInRight 0.5s ease-out ${delay}s forwards`,
    opacity: 0,
  });

  const scaleIn = (delay: number = 0) => ({
    animation: `scaleIn 0.4s ease-out ${delay}s forwards`,
    opacity: 0,
  });

  const staggerDelay = (index: number, baseDelay: number = 0.1) => ({
    animationDelay: `${index * baseDelay}s`,
  });

  onMounted(() => {
    setTimeout(() => {
      isVisible.value = true;
    }, 100);
  });

  return {
    isVisible,
    cardDelay,
    fadeInUp,
    fadeInLeft,
    fadeInRight,
    scaleIn,
    staggerDelay,
  };
}

/**
 * 数字滚动动画 Hook
 */
export function useCountUp() {
  const animateValue = (
    start: number,
    end: number,
    duration: number,
    callback: (value: number) => void
  ) => {
    const startTime = performance.now();
    const difference = end - start;

    const step = (currentTime: number) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const easeProgress = 1 - Math.pow(1 - progress, 3);
      const currentValue = start + difference * easeProgress;
      callback(Math.round(currentValue));

      if (progress < 1) {
        requestAnimationFrame(step);
      }
    };

    requestAnimationFrame(step);
  };

  return { animateValue };
}

/**
 * 悬停效果 Hook
 */
export function useHoverEffect() {
  const isHovered = ref(false);

  const handleMouseEnter = () => {
    isHovered.value = true;
  };

  const handleMouseLeave = () => {
    isHovered.value = false;
  };

  return {
    isHovered,
    handleMouseEnter,
    handleMouseLeave,
  };
}
